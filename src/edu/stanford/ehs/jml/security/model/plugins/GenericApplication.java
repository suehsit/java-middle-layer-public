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

package edu.stanford.ehs.jml.security.model.plugins;

import edu.stanford.ehs.jml.database.model.oracle.ConnectionManager;
import edu.stanford.ehs.jml.security.model.AuthResponse;
import edu.stanford.ehs.jml.security.model.Constants;
import edu.stanford.ehs.jml.security.model.Login;

import edu.stanford.ehs.jml.util.CSRFToken;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.sql.SQLWarning;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleTypes;
import oracle.jdbc.OracleResultSet;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import java.util.Vector;

import oracle.jdbc.OracleDatabaseMetaData;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Generic application login plugin
 */
public class GenericApplication implements Login {

    protected static Logger log = LogManager.getLogger(GenericApplication.class.getName());
    
    private static int QUERY_TIME_OUT = 120; // In seconds

    private AuthResponse authResponse = new AuthResponse();
    private long lastAccessed = 0;
    private long timeout = 0;
    private String account;
    private String username;
    private CSRFToken token;

    /**
     * Log in to JML using a user name and password. The username and passwords are send to the database
     * to determine if the credential are valid for that user in the context of the database account
     * they are trying to login to
     *
     * @param credentials Hashtable containing user name and password
     * @param timeout The timeout between a user's interactions with the JML
     * @param account The account id
     *
     * @return Formatted output from the login
     */
    public AuthResponse login(Hashtable credentials, long timeout, String account) {

        // set the timeout
        this.timeout = timeout;

        // Extract the user name and password
        String userName = (String)credentials.get(Constants.SECURITY_ATTR_SECURITY_SIMPLE_USERNAME);

        if (userName == null) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Missing user name");

            return (authResponse);
        }

        String password = (String)credentials.get(Constants.SECURITY_ATTR_SECURITY_SIMPLE_PASSWORD);

        if (password == null) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Missing password");

            return (authResponse);
        }

        this.account = account;

        // Create the authentication response
        ResultSet resultSet = null;
        java.sql.Connection connection = null;
        OracleCallableStatement callableStatement = null;
        Integer resultCount = 0;
        boolean isAuthenticated = false;
        String procedureSchema = "MY_APP";
        String procedureCatalog = "U_AUTH";
        String procedureNamePattern = "IS_AUTHENTICATED";
        String sqlString = "BEGIN " + procedureSchema + "." + procedureCatalog + "." + procedureNamePattern + "(?,?,?);END;";

        try {
            // Get the Oracle connection
            long timerStartGetConnection = System.currentTimeMillis();
            connection = ConnectionManager.getOracleConnection(log, account);
            long timerEndGetConnection = System.currentTimeMillis();
            log.info("Time getting connection: " + (timerEndGetConnection - timerStartGetConnection) + " ms");
            
            log.info("Time getting metadata: n/a (not retrieving metadata for this stored procedure)");
            
            // Check if the username and password provided by the user is valid for the current `account`
            callableStatement = (OracleCallableStatement) connection.prepareCall(sqlString);
            
            callableStatement.setString(1, userName);
            callableStatement.setString(2, password);
            callableStatement.registerOutParameter(3, OracleTypes.CURSOR); // returns a varchar
            
            // Set query timeout to two minutes which will prevent the server to freeze up
            // due to badly formatted queries
            callableStatement.setQueryTimeout(QUERY_TIME_OUT);
            
            // Keep the logging format consistant with the rest of the actions
            log.info("procedureSchema=" + procedureSchema);
            log.info("procedureCatalog=" + procedureCatalog);
            log.info("procedureNamePattern=" + procedureNamePattern);
            log.info("Found 2 parameters.");
            log.debug("Time-out: " + callableStatement.getQueryTimeout() + " seconds");
            log.info("SQL string: " + sqlString);
            
            log.info("P_USER = (1, 12)");
            log.info("Set input parameter 1 with " + userName);
            log.info("P_AUTHUSER = (1, 12)");
            log.info("Set input parameter 2 with [not logged]");
            log.info("AUTH_RESULT = (4, 1111)");
            
            long timerStartModelTime = System.currentTimeMillis();
            
            callableStatement.execute();
            
            resultSet = (OracleResultSet) callableStatement.getCursor(3);
            
            while (resultSet.next()) {
                isAuthenticated = resultSet.getBoolean(1);
                resultCount++;
            }
            
            long timerEndModelTime = System.currentTimeMillis();
            long timerDuration = timerEndModelTime - timerStartModelTime;
            log.info("Model time: " + timerDuration + " ms");
            log.info("View time: 0 ms");
            log.info("Total time: " + timerDuration + " ms");
            
            resultSet.close();
            connection.close();
            
            // This should return exactly one row, if it doesn't consider this a failed login attempt
            if (resultCount != 1) {
                authResponse.setSuccess(false);
                authResponse.setMessage("Login was not successful. Incorrect user name and password combination.");
            } else if (isAuthenticated) {
                this.username = userName;
                this.lastAccessed = System.currentTimeMillis();
                authResponse.setSuccess(true);
                authResponse.setMessage("Login successful for " + userName);
            } else {
                authResponse.setSuccess(false);
                authResponse.setMessage("Login was not successful. Incorrect user name and password combination.");
            }
        } catch (Exception e) {
            try {
                resultSet.close();
                connection.close();
                connection = null;
                log.error("Error during login: " + e.toString());
            } catch (Exception es) {
                log.debug("Difficulty in closing down connections: " + e.toString());
            }
        }

        return (authResponse);
    }

    /**
     * Log the user out from JML.
     *
     */
    public void logout() {
    }

    /**
     * Scramble two strings into one string.
     *
     * @param stringOne First string
     * @param stringTwo Second string
     *
     * @return Scrambled string
     */
    private String scrambler(String stringOne, String stringTwo) {
        StringBuffer stringThree = new StringBuffer();
        for (int i = 0; i < 20; i++)
            stringThree.append((char)((int)stringOne.charAt(i) ^ stringTwo.charAt(i)));
        return (stringThree.toString());
    }

    /**
     * Reset the user session's time-out timer.
     *
     * @return True is the session has not timed out, or false is the session has timed out.
     */
    public boolean touch() {
        long now = System.currentTimeMillis();

        log.debug("touching:" + this.username + "  idle:" + (now - this.lastAccessed) + "  timeout:" + this.timeout);

        // Time out the logical session letting a timeout value of 0 represent no time out
        if ((this.timeout > 0) && (now > (this.lastAccessed + this.timeout))) {
            logout();
            this.lastAccessed = now;
            return (false);
        } else {
            this.lastAccessed = now;
            return (true);
        }
    }

    /**
     * Return the account name that the user is logged into.
     *
     * @return Account name
     */
    public String getAccount() {
        return this.account;
    }

    /**
     * Set the account name for the login session.
     *
     * @param accountName Name of the account
     */
    public void setAccount(String accountName) {
        this.account = accountName;
    }
    
     /**
      * Return the CSRF token that the user is logged into.
      *
      * @return CSRF token
      */
     public CSRFToken getCSRFToken() {
         return token;
     }

     /**
      * Set the CSRF token for the login session.
      *
      * @param token CSRF token used for this session
      */
     public void setCSRFToken(CSRFToken token) {
         this.token = token;
     }

    /**
     * Return the date and time for when the user last send a request to the JML.
     *
     * @return Date and time of last acces
     */
    public Date getLastAccessed() {
        return (new Date(lastAccessed));
    }

    /**
     * Get the time-out.
     *
     * @return Time-out
     */
    public long getTimeout() {
        return timeout;
    }

    /**
     * Get the user id.
     *
     * @return User id
     */
    public String getUserId() {
        return this.username;
    }

    /**
     * Authorization based on action requested. All actions that pass through the Security Checkpoint
     * is allowed.
     *
     * @param action Requested action.
     * @return Always true
     *
     * @see edu.stanford.ehs.jml.security.model.SecurityManager#securityCheckPoint
     */
    public boolean isAuthorized(String action) {

        // Everybody can access any action
        return (true);
    }
}
