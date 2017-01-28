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

package edu.stanford.ehs.jml.security.model;

import edu.stanford.ehs.jml.core.model.CoreConstants;

import edu.stanford.ehs.jml.util.CSRFToken;

import java.io.File;

import java.net.InetAddress;
import java.net.NetworkInterface;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import edu.stanford.ehs.jml.database.model.oracle.Query;

/**
 * The security manager is responsible for managing the accounts and restricting access in accordance with
 * the specifications set forth in the server settings, account settings and logic in the security plug-ins.
 *
 * The JML runs in one of three discrete and customizable security modes which is set in
 * WEB-INF\conf\jml-server.xml. A server running in OnlyLocalRequest mode will only allow requests deriving from
 * the server's IP addresses, here including 127.0.0.1, in addition to the URI's specified in the
 * &lt;local-uri-definition&gt; tag. The mode AllRestrictedActions will only allow local and remote requests
 * with the actions specified in the &lt;allowed-remote-actions&gt; setting. PartialRestrictedActions will
 * allow all local requests as defined in OnlyLocalRequests and remote actions as defined in the
 * &lt;allowed-remote-actions&gt; tag.
 *
 * Example on a jml-server.xml configuration:
 * <p><blockquote><pre>
 * &lt;?xml version="1.0" encoding="UTF-8" ?&gt;
 * &lt;jml-server xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="jml-server-1.7.xsd"&gt;
 *   &lt;security-mode&gt;OnlyLocalRequests&lt;/security-mode&gt;
 *   &lt;!-- &lt;security-mode>AllRestrictedActions&lt;/security-mode&gt; --&gt;
 *   &lt;!-- &lt;security-mode>PartialRestrictedActions&lt;/security-mode&gt; --&gt;
 *   &lt;allowed-remote-actions&gt;
 *     &lt;action&gt;login&lt;/action&gt;
 *     &lt;action&gt;logout&lt;/action&gt;
 *     &lt;action&gt;doUserStoredProcedure&lt;/action&gt;
 *     &lt;action&gt;doEcho&lt;/action&gt;
 *   &lt;/allowed-remote-actions&gt;
 * &lt;/jml-server&gt;
 * </pre></blockquote></p>
 */
public class SecurityManager extends HttpServlet {

    protected static Logger log = LogManager.getLogger(SecurityManager.class.getName());

    private static boolean isSecurityAllRestrictedActions = false;
    private static boolean isSecurityOnlyLocalRequests = true;
    private static boolean isSecurityPartialRestrictedActions = false;
    private static String localIPAddress = null;
    
    private static boolean isInitialized = false;
    private static long accountsLastModified = 0;

    private static String LOCAL_HOST_IP = "127.0.0.1";

    private static Vector<String> uriExceptions = null;
    private static Vector<String> actionExceptions = null;

    private static Hashtable<String, Login> activeUserLogins = null;
    private static Hashtable<String, Account> accounts = null;
    @SuppressWarnings("compatibility:-7274125903335147754")
    private static final long serialVersionUID = 1L;

    /**
     * Is the server in the SecurityAllRestrictedActions mode?
     *
     * @return True or false
     */
    public static boolean isSecurityAllRestrictedActions() {
        return (isSecurityAllRestrictedActions);
    }

    /**
     * Is the server in the SecurityOnlyLocalRequests mode?
     *
     * @return True or false
     */
    public static boolean isSecurityOnlyLocalRequests() {
        return (isSecurityOnlyLocalRequests);
    }

    /**
     * Is the server in the SecurityPartialRestrictedActions mode?
     *
     * @return True or false
     */
    public static boolean isSecurityPartialRestrictedActions() {
        return (isSecurityPartialRestrictedActions);
    }


    /**
     * Is the request originating from the local server?
     *
     * @return True or false
     */
    private static boolean isLocalRequest(String remoteIPAddress, String uri) {
        return (remoteIPAddress.equals(getLocalIPAddress()) || remoteIPAddress.equals(LOCAL_HOST_IP) ||
                uriExceptions.contains(uri));
    }
    
    /**
     * Is the SecurityManager already initialized?
     *
     * @return True or false
     */
    public static boolean isInitialized() {
        return (isInitialized);
    }

    /**
     * Return the session id from the parameters Hashtable.
     * @param parameters Parameters including the session id
     * @return Session id
     */
    public static String getSessionId(Hashtable parameters) {
        String sessionId =
            ((HttpServletRequest)(parameters.get(CoreConstants.GENERAL_ATTR_HTTPREQUEST))).getSession().getId();

        return (sessionId);
    }

    /**
     * The security checkpoint is called by the controller for every request coming in. This is the basic implementation
     * of the three security modes
     *
     * @param actionCommand The action request
     * @param sessionId The session id
     * @param remoteIPAddress The IP address of the requestor
     * @param uri The URI of the requestor
     *
     * @return Null if the request is allowed; otherwise a text message is returned
     */
    public static String securityCheckPoint(String actionCommand, String sessionId, String remoteIPAddress,
                                            String uri) {

        if (isSecurityAllRestrictedActions && !(actionExceptions.contains(actionCommand))) {
            return ("Action " + actionCommand + " from " + getUserId(sessionId) +
                    " is not allowed in AllRestrictedActions mode");
        } else if (isSecurityOnlyLocalRequests && !isLocalRequest(remoteIPAddress, uri)) {
            return ("Action " + actionCommand + " from " + getUserId(sessionId) +
                    " is not allowed in OnlyLocalRequests mode");
        } else if (isSecurityPartialRestrictedActions && !isLocalRequest(remoteIPAddress, uri) &&
                   !(actionExceptions.contains(actionCommand))) {
            return ("Action " + actionCommand + " from " + getUserId(sessionId) +
                    " is not allowed in PartialRestrictedActions mode");
        }

        return (null);
    }


    /**
     * Generic login that forwards the login request to the login implementation specified in the account's
     * security class.
     *
     * @param controllerParameters Parameters here including user name and password for certain security plugins
     * @param accountId The account id
     * @param sessionId The session Id
     *
     * @return AuthResponse object containing the login result
     */
    public static AuthResponse login(Hashtable controllerParameters, String accountId, String sessionId) {
        Login loginInstance = null;
        String loginClassName = null;
        Boolean logDBMSOutput = null;
        long timeout = 0;

        if (log.isDebugEnabled()) {
            log.debug(" --> Login attempt");
            log.debug(" --> accountId=" + accountId);
            log.debug(" --> sessionId=" + sessionId);
            log.debug(" --> controllerParameters={username=" + controllerParameters.get("username") + ", password=[not logged]}");
        }

        try {
            if (accountId == null) {
                AuthResponse authResponse = new AuthResponse(false, "Please enter account identifier", null);
                return (authResponse);
            }

            // Log the user out, if the user is already logged in
            if (activeUserLogins.get(sessionId) != null) {
                log.debug("The user is already logged in. Logging out.");
                logout(sessionId);
            }

            // Get the login class, timeout, and if we should check for DBMS output
            loginClassName = accounts.get(accountId).getLoginClassName();
            timeout = accounts.get(accountId).getTimeOut();
            logDBMSOutput = accounts.get(accountId).getLogDBMSOutput();
            
            log.debug(" --> logDBMSOutput=" + logDBMSOutput);
            log.debug("Attempting to log in to the class " + loginClassName);

            // forward the login and catch a failed login
            Class loginClass = Class.forName(loginClassName);

            loginInstance = (Login)loginClass.newInstance();
        } catch (Exception e) {
            log.error("Error in logging in with the security class " + loginClassName + " for the account " +
                      accountId + ": " + e.toString());

            AuthResponse authResponse =
                new AuthResponse(false, "System failure during login. Please contact the Stanford EH&S administrator if this error persists", null);

            return (authResponse);
        }

        // Call the login command
        AuthResponse authResponse = loginInstance.login(controllerParameters, timeout, accountId);

        // Store the login instance in the active sessions table
        if (authResponse.isSuccess()) {
            CSRFToken token = new CSRFToken(120);
            loginInstance.setCSRFToken(token);
            activeUserLogins.put(sessionId, loginInstance);
            authResponse.setCSRFToken(token);
        }

        if (log.isDebugEnabled()) {
            log.debug(" -- --> current users logged in:");

            Enumeration userLogins = activeUserLogins.keys();

            while (userLogins.hasMoreElements()) {
                String thisKey = (String)userLogins.nextElement();
                Login thisLogin = activeUserLogins.get(thisKey);

                log.debug(" -- --> " + thisKey + ":" + thisLogin.getUserId());
            }
        }
        
        /*if (logDBMSOutput) { // @todo
            // Check if there is any DBMS output, and if so log it to the JML log file
            try {
                Query.getDBMSOutput(accountId);
            } catch (Exception e) {
            }
        }*/

        return (authResponse);
    }

    /**
     * Log the user out by removing the entry in active user logins.
     *
     * @param sessionId The session id to be logged out
     */
    public static void logout(String sessionId) {

        // log out using the Login logout method
        Login loginInstance = activeUserLogins.get(sessionId);

        if (loginInstance != null) {
            loginInstance.logout();

            // remove the Login from the active user logins table
            activeUserLogins.remove(sessionId);
        }
    }

    /**
     * Register an account with the JML. Used by the JML Server during initialization.
     *
     * @param account Account
     *
     * @see edu.stanford.ehs.jml.core.model.Server
     */
    public static void registerAccount(Account account) {
        accounts.put(account.getAccountId(), account);
    }

    /**
     * Retrieve an account from the registered accounts.
     *
     * @param accountId The id of the account
     *
     * @return Account
     */
    public static Account getAccount(String accountId) {
        return (accounts.get(accountId));
    }
    
    /**
     * Retrieve the current value for the accounts config last modified date
     *
     * @return int
     */
    public static long getAccountsLastModified() {
        return (accountsLastModified);
    }
    
    /**
     * Set the value of for the accounts config last modified date
     */
    public static void setAccountsLastModified(long timestamp) {
        log.debug("Setting accountsLastModified to " + timestamp);
        accountsLastModified = timestamp;
    }

    /**
     * Log out timed-out sessions.
     */
    protected static void deleteTimedOutSessions() {
        Enumeration userLogins = activeUserLogins.keys();

        while (userLogins.hasMoreElements()) {
            long now = System.currentTimeMillis();
            String thisKey = (String)userLogins.nextElement();
            Login thisLogin = activeUserLogins.get(thisKey);
            if ((thisLogin.getTimeout() > 0) &&
                (now > (thisLogin.getLastAccessed().getTime() + thisLogin.getTimeout()))) {
                log.debug("Logging " + thisLogin.getUserId() + " out");
                logout(thisKey);

            }
        }
    }

    /**
     * Reset the session's session timer.
     *
     * @param sessionId Session id
     * @param accountId Account id
     *
     * @return AuthResponse containing the results of the touch method
     */
    public static AuthResponse touch(String sessionId, String accountId) {
        String loginClassName = null;
        AuthResponse authResponse = new AuthResponse(true, "", null);

        try {
            if (accountId == null) {
                authResponse.setSuccess(false);
                authResponse.setMessage("Please enter account identifier");

                return (authResponse);
            }

            Login existingLogin = activeUserLogins.get(sessionId);

            if (!existingLogin.touch()) {
                activeUserLogins.remove(sessionId);
                authResponse.setSuccess(false);
                authResponse.setMessage("The user session timed out. Please login again");
            }
        } catch (Exception e) {
            log.error("Error in touching in with the security class " + loginClassName + " for the account " +
                      accountId + ": " + e.toString());
            authResponse.setSuccess(false);
            authResponse.setMessage("System failure during login. Please contact the Stanford EH&S administrator if this error persists");

            return (authResponse);
        }

        return (authResponse);
    }

    /**
     * Get all active user logins in a hashtable.
     *
     * @return Active user logins
     */
    public static Hashtable getActiveUserLogins() {
        return (activeUserLogins);
    }

    /**
     * Get the user id from the session id.
     *
     * @param sessionId Session Id
     * @return User id
     */
    public static String getUserId(String sessionId) {
        String userId = null;

        try {
            userId = activeUserLogins.get(sessionId).getUserId();
        } catch (Exception e) {
            log.error("Could not get the user id from the session " + sessionId);
        }

        return (userId);
    }

    /**
     * Get the account associated with a session.
     *
     * @param sessionId Session id
     * @return Account name
     */
    public static String getAccountName(String sessionId) {
        String accountName = null;

        try {
            accountName = activeUserLogins.get(sessionId).getAccount();
        } catch (Exception e) {
            log.error("Could not get the account name from the session " + sessionId);
        }

        return (accountName);
    }


    /**
     * Is the session authenticated?
     *
     * @param sessionId Session id
     * @return true if the session id is registered in the active user logins; otherwise, return false
     */
    public static boolean isAuthenticated(String sessionId) {
        boolean authenticationResult = false;

        if (activeUserLogins.get(sessionId) != null) {
            authenticationResult = true;
        }

        return (authenticationResult);
    }

    /**
     * Is the user authorized to perform the specified action?
     *
     * @param controllerParameters Controller parameters used in case where the user will be logged in seamlessly, e.g. WebAuth
     * @param accountId The account id
     * @param sessionId Session id
     * @param action The requested action
     * @return true if the user is authorized to perform the specified action
     */
    public static boolean isAuthorized(Hashtable controllerParameters, String accountId, String sessionId,
                                       String action) {

        Login loginInstance = activeUserLogins.get(sessionId);
        boolean authorizationResult = false;
        boolean useCSRFToken = true;

        if (activeUserLogins.get(sessionId) != null) {
            log.debug("ACCOUNTID=" + accountId);
            log.debug("CSRFTOKEN=" + (String)controllerParameters.get(CoreConstants.GENERAL_ATTR_CSRF_TOKEN));

            // At this point, we know that the user has logged in. Now, we will
            // check if the user is logged in to the specified account
            if (loginInstance.getAccount().equals(accountId)) {
                authorizationResult = loginInstance.isAuthorized(action);
            } else {
                // The user was not logged in to the specified account. The user should log out from the current account and log in to the specified account
                log.debug("The user was logged in - but not to the specified account");
            }
            
            useCSRFToken = accounts.get(accountId).getUseCSRFToken();
            
            // Check the CSRF token, but only if this account is using one (which it SHOULD!!)
            if (useCSRFToken) {
                // Compare the CSRF token that was passed in the querystring with what is in the login instance,
                // but only if the account is setup to require it (some debug accounts will not)
                if (!loginInstance.getCSRFToken().toString().equals( (String)controllerParameters.get(CoreConstants.GENERAL_ATTR_CSRF_TOKEN) )) {
                    authorizationResult = false;
                    log.debug("The CSRF token in the session was " + (String)controllerParameters.get(CoreConstants.GENERAL_ATTR_CSRF_TOKEN) + " but the user presented " + loginInstance.getCSRFToken().toString());
                }
            } else {
                log.debug(accountId + " does not require CSRF token (are you sure about this???)");
            }
        } else {
            log.debug("The user was not logged in - attempting to log in seamlessly");

            // Since some login schemes do not require an user interactive login,
            // we will attempt to login seamlessly
            AuthResponse authResponse = login(controllerParameters, accountId, sessionId);

            if (authResponse.isSuccess()) {
                if (activeUserLogins.get(sessionId) != null) {
                    loginInstance = (activeUserLogins.get(sessionId));
                    authorizationResult = loginInstance.isAuthorized(action);
                }
            }
        }

        return (authorizationResult);
    }


    /**
     * Return the local IP address.
     *
     * @return Local IP address
     */
    public static String getLocalIPAddress() {
        return (localIPAddress);
    }

    /**
     * Initialize the security manager.
     *
     * @param configFile Configuration file
     */
    public static void initialize(String configFile) {

        uriExceptions = new Vector<String>();
        actionExceptions = new Vector<String>();
        activeUserLogins = new Hashtable<String, Login>();
        accounts = new Hashtable<String, Account>();


        // Get local IP address
        getServerIPAddress();

        try {
            // Open the config file
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document xmlDocument = docBuilder.parse(new File(configFile));

            xmlDocument.getDocumentElement().normalize();

            // Get the URI exceptions
            NodeList listOfURIExceptions = xmlDocument.getElementsByTagName("uri");
            for (int i = 0; i < listOfURIExceptions.getLength(); i++) {
                Node uriException = listOfURIExceptions.item(i);
                NodeList uriExceptionTextList = uriException.getChildNodes();
                Node uriExceptionNode = (uriExceptionTextList.item(0));
                String uriExceptionTextValue = uriExceptionNode.getNodeValue().trim();
                uriExceptions.add(uriExceptionTextValue);
                log.debug("URI exception: " + uriExceptionTextValue);
            }

            // Get the action exceptions
            NodeList listOfActionExceptions = xmlDocument.getElementsByTagName("action");
            for (int i = 0; i < listOfActionExceptions.getLength(); i++) {
                Node actionException = listOfActionExceptions.item(i);
                NodeList actionExceptionTextList = actionException.getChildNodes();
                Node actionExceptionNode = actionExceptionTextList.item(0);
                String actionExceptionTextValue = actionExceptionNode.getNodeValue().trim();
                actionExceptions.add(actionExceptionTextValue);
                log.debug("Action exception: " + actionExceptionTextValue);
            }

            // Get the mode. The default is local mode
            NodeList securityNodeList = xmlDocument.getElementsByTagName("security-mode");
            Node firstSecurityNode = securityNodeList.item(0);
            NodeList firstSecurityNodeList = firstSecurityNode.getChildNodes();

            String securityMode = (firstSecurityNodeList.item(0)).getNodeValue().trim();

            log.debug("Security mode: " + securityMode);
            if (securityMode != null) {
                if (securityMode.equals(Constants.SECURITY_MODE_ALLRESTRICTEDACTIONS)) {
                    isSecurityAllRestrictedActions = true;
                    isSecurityOnlyLocalRequests = false;
                    isSecurityPartialRestrictedActions = false;
                } else if (securityMode.equals(Constants.SECURITY_MODE_PARTIALRESTRICTEDACTIONS)) {
                    isSecurityAllRestrictedActions = false;
                    isSecurityOnlyLocalRequests = false;
                    isSecurityPartialRestrictedActions = true;
                }
            }
            
            isInitialized = true;

        } catch (Exception e) {
            log.error("Error in parsing " + configFile + ": " + e.toString());
        }

    }
    
    /**
     * Re-initialize the security manager
     * Used by Server.java when it detects that the accounts file has changed
     */
    public static void reInitialize() {
        accounts = new Hashtable<String, Account>();
    }

    /**
     * Derive the server's IP address and save it in the localIPAddress attribute.
     */
    private static void getServerIPAddress() {
        // Get local IP address
        try {
            Enumeration<NetworkInterface> enumeratedNetworkInterface = NetworkInterface.getNetworkInterfaces();

            NetworkInterface networkInterface;
            Enumeration<InetAddress> enumeratedInetAddress;
            InetAddress inetAddress;

            for (; enumeratedNetworkInterface.hasMoreElements(); ) {
                networkInterface = enumeratedNetworkInterface.nextElement();
                enumeratedInetAddress = networkInterface.getInetAddresses();

                for (; enumeratedInetAddress.hasMoreElements(); ) {
                    inetAddress = enumeratedInetAddress.nextElement();
                    String anIPAddress = inetAddress.getHostAddress();
                    if (log.isDebugEnabled()) {
                        log.debug("Scanning local IP space: " + anIPAddress);
                    }

                    // exclude loop-back and MAC address
                    if (!(anIPAddress.equals("127.0.0.1") || anIPAddress.contains(":")))
                        localIPAddress = anIPAddress;
                }
            }
        } catch (Exception e) {
            log.error("Error in retrieving the local IP address: " + e.toString());
        }
    }

    /**
     * Return all active account names, i.e. the keys of the accounts vector.
     *
     * @return Active account names
     */
    public static Enumeration getActiveAccountNames() {
        return (accounts.keys());
    }
}
