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

package edu.stanford.ehs.jml.admin.controller;

import edu.stanford.ehs.jml.admin.model.Admin;
import edu.stanford.ehs.jml.admin.model.Constants;
import edu.stanford.ehs.jml.core.controller.ControllerTemplate;
import edu.stanford.ehs.jml.core.model.CoreConstants;
import edu.stanford.ehs.jml.security.model.AuthResponse;
import edu.stanford.ehs.jml.security.model.SecurityManager;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Module controller for the Admin module
 */
public class Controller extends ControllerTemplate {
    protected static Logger log = LogManager.getLogger(Controller.class.getName());
    protected static String VIEW_PACKAGE = "edu.stanford.ehs.jml.admin.view.";

    /**
     * Controller section of the logout function
     *
     * @param viewName The view to render the output
     * @param parameters The parameters for the logout function here including the session id of the user to be logged out
     * @param accountId The account that the user should be logged out from
     * @param sessionId Then HTTP session id for the user requesting the logout
     *
     * @return Formattted output of the model and view
     *
     * @see edu.stanford.ehs.jml.admin.model.Admin#logoutUser
     */
    public static String logout(String viewName, Hashtable parameters, String accountId, String sessionId) {
        String viewOutput = null;

        // Get the HttpServletRequest containing the parameters from the hashtable
        HttpServletRequest httpRequest = (HttpServletRequest)(parameters.get(CoreConstants.GENERAL_ATTR_HTTPREQUEST));

        // -----------------------------------------------
        // Security
        // -----------------------------------------------
        if (!SecurityManager.isAuthorized(parameters, accountId, sessionId, CoreConstants.ADMIN_CMND_LOGOUTUSER)) {
            viewOutput =
                    sendErrorToView(VIEW_PACKAGE + viewName, "Authorized login required for accessing the " + CoreConstants.ADMIN_CMND_LOGOUTUSER +
                                    " function", log);
        } else {
            AuthResponse authResponse = SecurityManager.touch(sessionId, accountId);

            if (!authResponse.isSuccess()) {
                viewOutput =
                        sendErrorToView(VIEW_PACKAGE + viewName, "Error in accessing the " + CoreConstants.ADMIN_CMND_LOGOUTUSER +
                                        " function: " + authResponse.getMessage(), log);
            } else {

                // -----------------------------------------------
                // Call the model
                // -----------------------------------------------
                String userSessionId = httpRequest.getParameter(Constants.ADMIN_ATTR_USER_SESSION_ID);

                Admin.logoutUser(userSessionId);

                // -----------------------------------------------
                // Call the view
                // -----------------------------------------------
                viewOutput =
                        callViewMethod(VIEW_PACKAGE + viewName, "logoutUser", new Object[] { userSessionId }, log);
            }
        }

        return (viewOutput);
    }

    /**
     * Retrieve the list of active users
     *
     * @param viewName The view to be used for rendering the output from the model function
     * @param parameters Parameter
     * @param accountId
     * @param sessionId
     *
     * @return Formatted output from the getActiveUsers function
     *
     * @see edu.stanford.ehs.jml.admin.model.Admin#getActiveUsers
     */
    public static String getActiveUsers(String viewName, Hashtable parameters, String accountId, String sessionId) {
        String viewOutput = null;

        // -----------------------------------------------
        // Security
        // -----------------------------------------------
        if (!SecurityManager.isAuthorized(parameters, accountId, sessionId,
                                          CoreConstants.ADMIN_CMND_GET_ACTIVE_USERS)) {
            viewOutput =
                    sendErrorToView(VIEW_PACKAGE + viewName, "Authorized login required for accessing the " + CoreConstants.ADMIN_CMND_GET_ACTIVE_USERS +
                                    " function", log);
        } else {
            AuthResponse authResponse = SecurityManager.touch(sessionId, accountId);

            if (!authResponse.isSuccess()) {
                viewOutput =
                        sendErrorToView(VIEW_PACKAGE + viewName, "Error in accessing the " + CoreConstants.ADMIN_CMND_GET_ACTIVE_USERS +
                                        " function: " + authResponse.getMessage(), log);
            } else {

                // -----------------------------------------------
                // Call the model
                // -----------------------------------------------
                Hashtable activeUsers = Admin.getActiveUsers();

                // -----------------------------------------------
                // Call the view
                // -----------------------------------------------
                viewOutput =
                        callViewMethod(VIEW_PACKAGE + viewName, "getActiveUsers", new Object[] { (Object)activeUsers },
                                       log);
            }
        }

        return (viewOutput);
    }
}
