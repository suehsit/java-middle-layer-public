/**
 * MIT License
 *
 * Copyright (c) 2017 The Board of Trustees of the Leland Stanford Junior University
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.stanford.ehs.jml.core.model;

import edu.stanford.ehs.jml.core.controller.Controller;
import edu.stanford.ehs.jml.core.controller.ControllerFactory;
import edu.stanford.ehs.jml.messaging.email.model.SMTPSettings;
import edu.stanford.ehs.jml.security.model.Account;
import edu.stanford.ehs.jml.security.model.SecurityManager;

import edu.stanford.ehs.jml.util.XMLUtil;

import java.io.File;

import java.util.Properties;
import java.util.Vector;

import javax.servlet.http.HttpServlet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.Timer;
import java.util.TimerTask;

/**
 * The Server class is the main servlet class responsible for initializing the JML server.
 *
 * Example of servlet specification in the web descriptor (web.xml):
 *
 * <pre>
 * <servlet>
 *  <servlet-name>server</servlet-name>
 *  <servlet-class>edu.stanford.ehs.jml.core.model.Server</servlet-class>
 *  <init-param>
 *    <param-name>config-file</param-name>
 *    <param-value>WEB-INF/conf/jml-server.xml</param-value>
 *  </init-param>
 *  <init-param>
 *    <param-name>accounts-file</param-name>
 *    <param-value>WEB-INF/conf/accounts.xml</param-value>
 *  </init-param>
 *  <init-param>
 *    <param-name>min-thread-pool</param-name>
 *    <param-value>5</param-value>
 *  </init-param>
 *  <load-on-startup>2</load-on-startup>
 * </servlet>
 * </pre>
 */
public class Server extends HttpServlet {

    protected static Logger log = LogManager.getLogger(Server.class.getName());
    private static Vector<Controller> threads = null;
    private static int threadCounter = 0;
    @SuppressWarnings("compatibility:240687095296851242")
    private static final long serialVersionUID = 1L;
    
    String prefix = null;
    String accountsFilename = null;
    String configFilename = null;
    
    Timer acTimer;
    protected long acDelay = 0;

    /**
     * Initialize the Server servlet
     */
    public void init() {

        threads = new Vector<Controller>();

        log.info("==============================================================");
        log.info(CoreConstants.PRODUCT_NAME);
        log.info(CoreConstants.PRODUCT_OWNER);
        log.info("Version " + CoreConstants.PRODUCT_VERSION);
        log.info(CoreConstants.PRODUCT_DATE);

        // Retrieve the servlet parameters
        prefix = getServletContext().getRealPath("/");
        accountsFilename = getInitParameter("accounts-file");
        configFilename = getInitParameter("config-file");
        
        File file = new File(prefix + accountsFilename);
        
        if (!file.exists()) {
            // The accounts file does not exist
            log.fatal("Unable to locate the accounts file");
            log.fatal("**** HALTING JML SERVER ****");
            System.exit(1);
        }
        
        if (!ControllerFactory.isInitialized()) {
            // Setting up the thread pool
            ControllerFactory.initialize();
        }
        
        if (!SecurityManager.isInitialized()) {
            // Initialize the security manager
            SecurityManager.initialize(prefix + configFilename);
        } else {
            // Re-initialize the security manager
            SecurityManager.reInitialize();
        }

        // Check for correct security manager initialization
        if (SecurityManager.getLocalIPAddress() == null) {
            log.fatal("Unable to retrieve a local IP address");
            log.fatal("**** HALTING JML SERVER ****");
            System.exit(1);
        }

        if (SecurityManager.isSecurityAllRestrictedActions()) {
            log.debug("The JML server is running in AllRestrictedActions mode (" +
                      SecurityManager.getLocalIPAddress() + ")");
        } else if (SecurityManager.isSecurityOnlyLocalRequests()) {
            log.debug("The JML server is running in OnlyLocalRequests mode (" + SecurityManager.getLocalIPAddress() +
                      ")");

        } else {
            log.debug("The JML server is running in PartialRestrictedActions mode (" +
                      SecurityManager.getLocalIPAddress() + ")");
        }

        // Initialize the accounts
        if (log.isDebugEnabled()) {
            log.debug("Initializing accounts from " + prefix + accountsFilename);
        }
        try {

            // create the connections
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document xmlDocument = docBuilder.parse(new File(prefix + accountsFilename));

            xmlDocument.getDocumentElement().normalize();
            
            // Check if the accounts file should be checked for updates periodically
            // The value is in milliseconds
            // If the XML element doesn't exist, disable checking for updates
            try {
                NodeList checkModifiedNodeList = xmlDocument.getElementsByTagName("check-modified");
                Node firstCheckModifiedNode = checkModifiedNodeList.item(0);
                NodeList firstCheckModifiedNodeList = firstCheckModifiedNode.getChildNodes();
                
                Element checkModifiedElement = (Element)checkModifiedNodeList.item(0);
                String isCheckModifiedActive = checkModifiedElement.getAttribute("active");
                
                if (isCheckModifiedActive.toLowerCase().equals("true")) {
                    acDelay = Long.parseLong(firstCheckModifiedNodeList.item(0).getNodeValue().trim());
                }
            } catch (Exception e) {
                acDelay = 0;
                log.debug("`check-modified` element not found; disabling checking the accounts file for updates");
            }

            NodeList listOfAccountNodes = xmlDocument.getElementsByTagName("account");

            for (int i = 0; i < listOfAccountNodes.getLength(); i++) {
                Node firstAccountNode = listOfAccountNodes.item(i);

                if (firstAccountNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element firstAccountElement = (Element)firstAccountNode;
                    NodeList connectionNodeList = firstAccountElement.getElementsByTagName("connection");
                    Element firstConnectionElement = (Element)connectionNodeList.item(0);
                    String isActive = firstAccountElement.getAttribute("active");
                    String id = firstAccountElement.getAttribute("id").toLowerCase();
                    String name = XMLUtil.getTagValue(firstAccountElement, "name");

                    if (isActive.toLowerCase().equals("true")) {
                        log.debug("Active: " + name + " (" + id + ")");
                        log.debug(id + ": Initializing data source");

                        Account account = new Account();

                        // Initialize the data source
                        OracleConnectionPoolDataSource oracleDataSource = new OracleConnectionPoolDataSource();

                        oracleDataSource.setServerName(XMLUtil.getTagValue(firstConnectionElement, "server-name"));
                        oracleDataSource.setServiceName(XMLUtil.getTagValue(firstConnectionElement, "database-name"));
                        oracleDataSource.setPortNumber(new Integer(XMLUtil.getTagValue(firstConnectionElement,
                                                                                       "port")).intValue());
                        oracleDataSource.setDriverType("thin");
                        oracleDataSource.setUser(XMLUtil.getTagValue(firstConnectionElement, "user-name"));
                        oracleDataSource.setPassword(XMLUtil.getTagValue(firstConnectionElement, "password"));

                        oracleDataSource.setDriverType(XMLUtil.getTagValue(firstConnectionElement, "driver-type"));
                        oracleDataSource.setNetworkProtocol(XMLUtil.getTagValue(firstConnectionElement,
                                                                                "network-protocol"));
                        oracleDataSource.setDescription(name);

                        Properties connectionCacheProperties = new Properties();

                        // --------- InitialLimit ---------
                        try {
                            connectionCacheProperties.setProperty("InitialLimit",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "initial-limit"));
                            log.debug(id + ": Setting InitialLimit to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "initial-limit"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("InitialLimit", "0");
                            log.debug(id + ": Setting InitialLimit to 0 (default)");
                        }

                        // --------- MaxLimit ---------
                        try {
                            connectionCacheProperties.setProperty("MaxLimit",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "max-limit"));
                            log.debug(id + ": Setting MaxLimit to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "max-limit"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("MaxLimit",
                                                                  (new Integer(Integer.MAX_VALUE)).toString());
                            log.debug(id + ": Setting MaxLimit to " + Integer.MAX_VALUE + " (default)");
                        }

                        // --------- MaxStatementsLimit ---------
                        try {
                            connectionCacheProperties.setProperty("MaxStatementsLimit",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "max-statements-limit"));
                            log.debug(id + ": Setting MaxStatementsLimit to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "imax-statements-limit"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("MaxStatementsLimit", "0");
                            log.debug(id + ": Setting MaxStatementsLimit to 0 (default)");
                        }

                        // --------- MinLimit ---------
                        try {
                            connectionCacheProperties.setProperty("MinLimit",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "min-limit"));
                            log.debug(id + ": Setting MinLimit to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "min-limit"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("MinLimit", "0");
                            log.debug(id + ": Setting MinLimit to 0 (default)");
                        }

                        // --------- InactivityTimeout ---------
                        try {
                            connectionCacheProperties.setProperty("InactivityTimeout",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "inactivity-timeout"));
                            log.debug(id + ": Setting InactivityTimeout to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "inactivity-timeout"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("InactivityTimeout", "0");
                            log.debug(id + ": Setting InactivityTimeout to 0 (default)");
                        }

                        // --------- TimeToLiveTimeout ---------
                        try {
                            connectionCacheProperties.setProperty("TimeToLiveTimeout",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "time-to-live-timeout"));
                            log.debug(id + ": Setting TimeToLiveTimeout to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "time-to-live-timeout"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("TimeToLiveTimeout", "0");
                            log.debug(id + ": Setting TimeToLiveTimeout to 0 (default)");
                        }

                        // --------- AbandonedConnectionTimeout ---------
                        try {
                            connectionCacheProperties.setProperty("AbandonedConnectionTimeout",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "abandoned-connection-timeout"));
                            log.debug(id + ": Setting InitialLimit to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "abandoned-connection-timeout"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("AbandonedConnetionTimeout", "0");
                            log.debug(id + ": Setting AbandonedConnectionTimeout to 0 (default)");
                        }

                        // --------- PropertyCheckInterval ---------
                        try {
                            connectionCacheProperties.setProperty("PropertyCheckInterval",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "property-check-interval"));
                            log.debug(id + ": Setting PropertyCheckInterval to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "property-check-interval"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("PropertyCheckInterval", "0");
                            log.debug(id + ": Setting PropertyCheckInterval to 0 (default)");
                        }

                        // --------- LowerThresholdLimit ---------
                        try {
                            connectionCacheProperties.setProperty("LowerThresholdLimit",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "lower-threshold-limit"));
                            log.debug(id + ": Setting LowerThresholdLimit to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "lower-threshold-limit"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("LowerThresholdLimit", "0");
                            log.debug(id + ": Setting LowerThresholdLimit to 0 (default)");
                        }

                        // --------- ValidateConnection ---------
                        try {
                            connectionCacheProperties.setProperty("ValidateConnection",
                                                                  XMLUtil.getTagValue(firstConnectionElement,
                                                                                      "validate-connection"));
                            log.debug(id + ": Setting ValidateConnection to " +
                                      XMLUtil.getTagValue(firstConnectionElement, "validate-connection"));
                        } catch (Exception e) {
                            connectionCacheProperties.setProperty("ValidateConnection", "false");
                            log.debug(id + ": Setting ValidateConnection to false (default)");
                        }
                        
                        log.debug("Adding connection properties to dbpool");
                        oracleDataSource.setConnectionProperties(connectionCacheProperties);

                        log.debug(id + ": Storing account in the static storage");
                        account.setConnectionPoolDataSource(oracleDataSource);

                        // Get the security settings
                        NodeList securityNodeList = firstAccountElement.getElementsByTagName("security");
                        Element firstSecurityElement = (Element)securityNodeList.item(0);
                        boolean useCSRFToken = true;

                        // Register the account with the access manager
                        String accessClassName = XMLUtil.getTagValue(firstSecurityElement, "access-control-class");
                        long sessionTimeOut = 0;

                        try {
                            sessionTimeOut =
                                    Long.parseLong(XMLUtil.getTagValue(firstSecurityElement, "session-timeout").trim());
                        } catch (Exception e) { /* Ignore errors */
                            log.error("Error in converting the session-time value to Integer: " +
                                      XMLUtil.getTagValue(firstSecurityElement, "session-timeout"));
                        }
                        
                        try {
                            String useCSRFTokenString = XMLUtil.getTagValue(firstSecurityElement, "use-csrf-token");

                            if (useCSRFTokenString == null) {
                                useCSRFToken = true;
                            } else {
                                if (useCSRFTokenString.equalsIgnoreCase("false")) {
                                    useCSRFToken = false;
                                }
                            }
                        } catch (Exception e) {
                            log.debug("Was not able to retrieve use-csrf-token value for account " + id);
                        }
                        
                        log.debug(id + ": Setting useCSRFToken to " + useCSRFToken);
                        
                        account.setAccountId(id);
                        account.setLoginClassName(accessClassName);
                        account.setUseCSRFToken(useCSRFToken);
                        account.setTimeOut(sessionTimeOut * 1000 * 60); // From minutes to milliseconds

                        // Load email settings
                        log.debug(id + ": Setting SMTP server");
                        SMTPSettings smtpSettingsInstance = new SMTPSettings();
                        String smtpServer = new String();
                        String smtpUser = new String();
                        String smtpPassword = new String();
                        boolean smtpDebug = false;
                        NodeList emailNodeList = firstAccountElement.getElementsByTagName("email");

                        if (emailNodeList != null) {
                            Element firstEmailElement = (Element)emailNodeList.item(0);

                            try {
                                smtpServer = XMLUtil.getTagValue(firstEmailElement, "smtp-server");
                            } catch (Exception e) {
                                log.warn("Problems in retrieving smtp-server for the account " + id);
                            }

                            try {
                                smtpUser = XMLUtil.getTagValue(firstEmailElement, "smtp-user");
                            } catch (Exception e) {
                                log.debug("Was not able to retrieve smtp-user for the account " + id);
                            }

                            try {
                                smtpPassword = XMLUtil.getTagValue(firstEmailElement, "smtp-password");
                            } catch (Exception e) {
                                log.debug("Was not able to retrieve smtp-password for the account " + id);
                            }

                            try {
                                String smtpDebugString = XMLUtil.getTagValue(firstEmailElement, "smtp-debug");

                                if (smtpDebugString == null) {
                                    smtpDebug = false;
                                } else {
                                    if (smtpDebugString.equalsIgnoreCase("true")) {
                                        smtpDebug = true;
                                    }
                                }
                            } catch (Exception e) {
                                log.debug("Was not able to retrieve smtp-debug for the account " + id);
                            }

                            if (smtpServer != null) {
                                smtpSettingsInstance.setSmtpServer(smtpServer);
                            }

                            if (smtpUser != null) {
                                smtpSettingsInstance.setSmtpUserName(smtpUser);
                            }

                            if (smtpPassword != null) {
                                smtpSettingsInstance.setSmtpPassword(smtpPassword);
                            }
                        }

                        smtpSettingsInstance.setDebug(smtpDebug);
                        account.setSMTPSettings(smtpSettingsInstance);
                        
                        // Check if DBMS output logging should be enabled, but only if the current account is marked active
                        if (isActive.toLowerCase().equals("true")) {
                            try {
                                String logDBMSOutput = XMLUtil.getTagValue(firstAccountElement, "log-dbms-output");
                                
                                if (logDBMSOutput != null) {
                                    if (logDBMSOutput.equals("true")) {
                                        log.debug(id + ": Setting DBMS output logging to true");
                                        account.setLogDBMSOutput(true);
                                    } else {
                                        log.debug(id + ": Setting DBMS output logging to false");
                                        account.setLogDBMSOutput(false);
                                    }
                                } else {
                                    log.debug(id + ": Setting DBMS output logging to false");
                                    account.setLogDBMSOutput(false);
                                }
                            } catch (Exception e) {
                                log.debug("Was not able to retrieve log-dbms-output value for account " + id);
                                account.setLogDBMSOutput(false);
                            }
                        } else {
                            log.debug(id + ": DBMS output will NOT be logged; the account is not active");
                        }

                        // Register the account with the access manager
                        log.debug(id + ": Register account with the JML security manager");
                        SecurityManager.registerAccount(account);

                    } else {
                        log.debug("Not Active: " + name + " (" + id + ")");
                    }
                }
            }
            
            // Only setup a timer to check the accounts file for updates if the poll time is not `0`
            if (acDelay > 0) {
                log.debug("Checking the accounts file for changes every " + acDelay + " milliseconds");
                SecurityManager.setAccountsLastModified(file.lastModified());
                scheduleAccountsTimer();
            } else {
                log.debug("Checking the accounts file for changes is disabled");
            }
        } catch (Exception e) {
            log.error("Error in parsing " + accountsFilename + ": " + e.toString());
        }
        log.info("==============================================================");

    }

    public void destroy() {
        ControllerFactory.destroy();
    }

    /**
     * Generate a thread ID. This method is mainly used by adapters.
     *
     * @return hexadecimal thread ID
     */
    public synchronized static String generateThreadId() {
        return (Integer.toHexString(threadCounter++));
    }
    
    /**
     * Initialize the timer that will check the accounts file for changes
     */
    private void scheduleAccountsTimer() {
        try {
            // Set up the timer
            TimerTask accountsConfigLastModifiedTask = new AccountsConfigLastModifiedTask();
            acTimer = new Timer();
            acTimer.scheduleAtFixedRate(accountsConfigLastModifiedTask, acDelay, acDelay);
        } catch (Exception e) {
            log.error("Error in initializing the timer: " + e.toString());
        }
    }
    
    /**
     * Private class containing the timed task that checks the accounts file 
     * to see if the last modified date is different than the last time that
     * file was loaded (most likely when the JML was first initialied), and
     * if so reload it
     * 
     */
    public class AccountsConfigLastModifiedTask extends TimerTask {
        public void run() {
            // Check the last modified date for the accounts file
            File file = new File(prefix + accountsFilename);
            long currentModifiedDate = SecurityManager.getAccountsLastModified();
            long newModifiedDate = file.lastModified();
            
            if (currentModifiedDate < newModifiedDate) {
                log.info("Accounts file was modified, reloading");
                
                // Cancel the existing timer so it won't compete with the new
                // one that's created after calling init() again
                acTimer.cancel();
                
                init();
            }
        }
    }

}
