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

import edu.stanford.ehs.jml.util.CSRFToken;

import java.util.Date;
import java.util.Hashtable;
//import edu.stanford.ehs.jml.util.CSRFToken;

/**
 * Interface for security plug-ins.
 */
public interface Login {

    /**
     * Log in to the specified account with the specified credentials.
     *
     * @param credentials Hashtable containing user name and password
     * @param timeout The timeout between a user's interactions with the JML
     * @param account The account id
     *
     * @return Formatted output from the login
     */
    public AuthResponse login(Hashtable credentials, long timeout, String account);

    /**
     * Log the user out from JML.
     *
     */
    public void logout();

    /**
     * Reset the user session's time-out timer.
     *
     * @return True is the session has not timed out, or false is the session has timed out.
     */
    public boolean touch();

    /**
     * Return the account name that the user is logged into.
     *
     * @return Account name
     */
    public String getAccount();

    /**
     * Set the account name for the login session.
     *
     * @param accountName Name of the account
     */
    public void setAccount(String accountName);
    
    /**
     * Return the CSRF token that the user is logged into.
     *
     * @return CSRF token
     */
    public CSRFToken getCSRFToken();

    /**
     * Set the CSRF token for the login session.
     *
     * @param token CSRF token used for this session
     */
    public void setCSRFToken(CSRFToken token);

    /**
     * Return the date and time for when the user last send a request to the JML.
     *
     * @return Date and time of last access
     */

    /**
     * Get the time-out.
     *
     * @return Time-out
     */
    public long getTimeout();

    /**
     * Get the user id.
     *
     * @return User id
     */
    public String getUserId();

    /**
     * Authorization based on action requested.
     *
     * @param action Requested action.
     * @return Always true
     *
     * @see edu.stanford.ehs.jml.security.model.SecurityManager#securityCheckPoint
     */
    public boolean isAuthorized(String action);

    /**
     * Return the date and time for when the user last send a request to the JML.
     *
     * @return Date and time of last acces
     */
    public Date getLastAccessed();
}
