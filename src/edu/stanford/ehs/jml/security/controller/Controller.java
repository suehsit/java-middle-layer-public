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

package edu.stanford.ehs.jml.security.controller;

import edu.stanford.ehs.jml.core.controller.ControllerTemplate;
import edu.stanford.ehs.jml.core.model.CoreConstants;
import edu.stanford.ehs.jml.security.model.AuthResponse;
import edu.stanford.ehs.jml.security.model.Constants;
import edu.stanford.ehs.jml.security.model.SecurityManager;

import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Module controller for the security module
 */
public class Controller extends ControllerTemplate {

    protected static Logger log = LogManager.getLogger(Controller.class.getName());
    protected static String VIEW_PACKAGE = "edu.stanford.ehs.jml.security.view.";

    /**
     * Login in to the JML by extracting user name and password (only used in certain security plugins) and
     * the account and then call the login method in the SecurityManager.
     *
     * @param viewName The view name to be used for rendering the output from the Security Manager
     * @param parameters The parameters for the login including user name, password and account
     * @param sessionId The session id of the login requestor
     *
     * @return Formatted output from the SecurityManager result
     *
     * @see edu.stanford.ehs.jml.security.model.SecurityManager#login
     */
    public static String login(String viewName, Hashtable parameters, String sessionId) {

        // -----------------------------------------------
        // Extract parameters
        // -----------------------------------------------
        Hashtable<String, String> credentials =
            new Hashtable<String, String>(2); // Initial capacity to hold user name and password
        String simplePassword = (String)parameters.get(Constants.SECURITY_ATTR_SECURITY_SIMPLE_PASSWORD);

        if (simplePassword != null) {
            credentials.put(Constants.SECURITY_ATTR_SECURITY_SIMPLE_PASSWORD, simplePassword);
        }

        String simpleUserName = (String)parameters.get(Constants.SECURITY_ATTR_SECURITY_SIMPLE_USERNAME);

        if (simpleUserName != null) {
            credentials.put(Constants.SECURITY_ATTR_SECURITY_SIMPLE_USERNAME, simpleUserName);
        }

        String accountId = ((String)parameters.get(CoreConstants.GENERAL_ATTR_ACCOUNT_ID)).toLowerCase();

        // -----------------------------------------------
        // Call the model
        // -----------------------------------------------
        AuthResponse authResponse = SecurityManager.login(credentials, accountId, sessionId);

        // -----------------------------------------------
        // Call the view
        // -----------------------------------------------
        String viewOutput = callViewMethod(VIEW_PACKAGE + viewName, "login", new Object[] { authResponse }, log);

        return (viewOutput);
    }

    /**
     * Logout from the JML.
     *
     * @param viewName The view name to be used for rendering the output from the Security Manager
     * @param sessionId The session id of the login requestor
     *
     * @return Formatted output from the SecurityManager result
     *
     * @see edu.stanford.ehs.jml.security.model.SecurityManager#logout
     */
    public static String logout(String viewName, String sessionId) {

        // -----------------------------------------------
        // Call the model
        // -----------------------------------------------
        SecurityManager.logout(sessionId);

        // -----------------------------------------------
        // Call the view
        // -----------------------------------------------
        String viewOutput = callViewMethod(VIEW_PACKAGE + viewName, "logout", null, log);

        return (viewOutput);
    }
}
