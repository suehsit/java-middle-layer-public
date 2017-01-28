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
import edu.stanford.ehs.jml.security.model.Constants;
import edu.stanford.ehs.jml.security.model.Login;

import edu.stanford.ehs.jml.util.CSRFToken;

import java.util.Date;
import java.util.Hashtable;

/**
 * Implementation of a rudimentary login schema
 */
public class SimpleLogin implements Login {

    private AuthResponse authResponse = new AuthResponse();
    private long lastAccessed = 0;
    private long timeout = 0;
    private String account;
    private String username;
    private CSRFToken token;

    /**
     * Log in to JML
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

        // Create the authentication response
        if (userName.equals("pop") && password.equals("bob")) {
            this.username = userName;
            authResponse.setSuccess(true);
            authResponse.setMessage("Login successful");
        } else {
            authResponse.setSuccess(false);
            authResponse.setMessage("Login was not successful. Incorrect user name and password combination.");
        }

        return (authResponse);
    }

    /**
     * Log the user out from JML.
     */
    public void logout() {
    }

    /**
     * Reset the user session's time-out timer.
     *
     * @return True is the session has not timed out, or false is the session has timed out.
     */
    public boolean touch() {
        long now = System.currentTimeMillis();

        this.lastAccessed = now;

        if ((this.timeout > 0) && (now > (this.lastAccessed + this.timeout))) {

            // Time out the logical session, letting a timeout value of 0 represent no time out
            return (false);
        } else {
            return (true);
        }
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
     * Return the account name that the user is logged into.
     *
     * @return Account name
     */
    public String getAccount() {
        return this.account;
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
        if (action.equals(CoreConstants.RUNTIME_CMND_SESSINFO) ||
            action.equals(CoreConstants.DATABASE_CMND_USERSTOREDPROCEDURE) ||
            action.equals(CoreConstants.EMAIL_CMND_EMAIL_EMAIL)) {
            return (true);
        } else {
            return (false);
        }
    }
}
