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

import edu.stanford.ehs.jml.core.model.CoreConstants;
import edu.stanford.ehs.jml.security.model.AuthResponse;
import edu.stanford.ehs.jml.security.model.Login;

import edu.stanford.ehs.jml.util.CSRFToken;

import java.util.Date;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of the Stanford WebAuth security plug-in for JML
 */
public class WebAuth implements Login {
    protected static Logger log = LogManager.getLogger(SecurityManager.class.getName());

    private static String WEBAUTH_USER = "WEBAUTH_USER";
    private static String WEBAUTH_TOKEN_EXPIRATION = "WEBAUTH_TOKEN_EXPIRATION";
    private static String WEBAUTH_TOKEN_CREATION = "WEBAUTH_TOKEN_CREATION";
    private static String WEBAUTH_REMOTE_USER = "REMOTE_USER";
    private static String WEBAUTH_AUTH_TYPE = "AUTH_TYPE";
    private static String WEBAUTH_AUTH_UNSET = "<UNSET>";

    private AuthResponse authResponse = new AuthResponse();
    private long lastAccessed = 0;
    private long timeout = 0;
    private String account;
    private String username;
    private CSRFToken token;

    /**
     * Log in to JML using integrated WebAuth login. Logins to the JML are accepted if the session variable
     * WEBAUTH_USER is not empty. The JML user name will be the value of WEBAUTH_USER.
     *
     * @param credentials Not used
     * @param timeout The timeout between a user's interactions with the JML
     * @param account The account id
     *
     * @return Formatted output from the login
     */
    public AuthResponse login(Hashtable credentials, long timeout, String account) {

        // set the timeout
        this.timeout = timeout;

        // set account timeout
        this.account = account;

        HttpServletRequest httpServletRequest =
            (HttpServletRequest)credentials.get(CoreConstants.GENERAL_ATTR_HTTPREQUEST);

        // Extract the user name and password
        if (httpServletRequest != null) {
            username = (String)httpServletRequest.getAttribute(WEBAUTH_USER);

            String remoteUser = (String)httpServletRequest.getAttribute(WEBAUTH_REMOTE_USER);
            String authType = (String)httpServletRequest.getAttribute(WEBAUTH_AUTH_TYPE);
            String webAuthTokenCreation = (String)httpServletRequest.getAttribute(WEBAUTH_TOKEN_CREATION);
            String webAuthTokenExpiration = (String)httpServletRequest.getAttribute(WEBAUTH_TOKEN_EXPIRATION);

            if (log.isDebugEnabled()) {
                log.debug("account = " + account);
                log.debug("username = " + username);
                log.debug("remoteUser = " + remoteUser);
                log.debug("authType = " + authType);
                log.debug("webAuthTokenCreation = " + webAuthTokenCreation);
                log.debug("webAuthTokenExpiration = " + webAuthTokenExpiration);
            }
        } else {
            log.error("The httpServletRequest is empty");
        }

        if (username == null) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Missing user name");
            log.debug("WebAuth login result: " + authResponse.getMessage());
        } else if (username.equals(WEBAUTH_AUTH_UNSET)) {
            authResponse.setSuccess(false);
            authResponse.setMessage("User was not logged in with WebAuth");
            log.debug("WebAuth login result: " + authResponse.getMessage());
        } else if (account == null) {
            authResponse.setSuccess(false);
            authResponse.setMessage("Missing account name");
            log.debug("WebAuth login result: " + authResponse.getMessage());
        } else {
            authResponse.setSuccess(true);
            authResponse.setMessage("Login successful");
            log.debug("WebAuth login result: " + authResponse.getMessage());
        }

        return (authResponse);
    }

    /**
     * Log the user out from JML.
     *
     */
    public void logout() {
        username = null;
    }

    /**
     * Reset the user session's time-out timer with no time-out options
     *
     * @return True
     */
    public boolean touch() {
        long now = System.currentTimeMillis();

        this.lastAccessed = now;

        return (true);
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
        return (true);
    }
}
