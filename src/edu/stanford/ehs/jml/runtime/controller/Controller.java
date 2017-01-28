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

package edu.stanford.ehs.jml.runtime.controller;

import edu.stanford.ehs.jml.core.controller.ControllerTemplate;
import edu.stanford.ehs.jml.core.model.CoreConstants;
import edu.stanford.ehs.jml.runtime.model.Constants;
import edu.stanford.ehs.jml.runtime.model.Runtime;
import edu.stanford.ehs.jml.security.model.AuthResponse;
import edu.stanford.ehs.jml.security.model.SecurityManager;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Module controller for the runtime module
 */
public class Controller extends ControllerTemplate {
    protected static Logger log = LogManager.getLogger(Controller.class.getName());
    protected static String VIEW_PACKAGE = "edu.stanford.ehs.jml.runtime.view.";

    /**
     * Controller for resetSession function.
     *
     * @param viewName The name of the view to render the output from the model
     * @param parameters Paramaters including the HTTP session
     * @param accountId The requesting account
     * @param sessionId The session Id of the requesting session Id
     *
     * @return Formatted output from the resetSession function
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#resetSession
     */
    public static String resetSession(String viewName, Hashtable parameters, String accountId, String sessionId) {
        String viewOutput;

        // -----------------------------------------------
        // Extract parameters
        // -----------------------------------------------
        // Get the HttpServletRequest containing the parameters from the hashtable
        HttpServletRequest httpRequest = (HttpServletRequest)(parameters.get(CoreConstants.GENERAL_ATTR_HTTPREQUEST));

        // -----------------------------------------------
        // Security
        // -----------------------------------------------
        if (!SecurityManager.isAuthorized(parameters, accountId, sessionId, CoreConstants.RUNTIME_CMND_APPRESET)) {
            viewOutput =
                    sendErrorToView(VIEW_PACKAGE + viewName, "Authorized login required for accessing the " + CoreConstants.RUNTIME_CMND_APPRESET +
                                    " function", log);
        } else {
            AuthResponse authResponse = SecurityManager.touch(sessionId, accountId);

            if (!authResponse.isSuccess()) {
                viewOutput =
                        sendErrorToView(VIEW_PACKAGE + viewName, "Error in accessing the " + CoreConstants.RUNTIME_CMND_APPRESET +
                                        " function: " + authResponse.getMessage(), log);
            } else {

                // -----------------------------------------------
                // Call the model
                // -----------------------------------------------
                Runtime.resetSession(httpRequest.getSession(true));

                // -----------------------------------------------
                // Call the view
                // -----------------------------------------------
                viewOutput = callViewMethod(VIEW_PACKAGE + viewName, "resetSession", null, log);
            }
        }

        return (viewOutput);
    }

    /**
     * Controller element of the getRequestInfo function.
     *
     * @param viewName The name of the view to render the ouput from the model function
     * @param parameters The parameters for the model function and the security
     * @param accountId The name of the account in which context the function should be executed
     * @param sessionId The session id of the client
     *
     * @return Formatted output of the getRequestInfo
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#getRequestInfo
     */
    public static String getRequestInfo(String viewName, Hashtable parameters, String accountId, String sessionId) {

        // -----------------------------------------------
        // Extract parameters
        // -----------------------------------------------
        // Get the HttpServletRequest containing the parameters from the hashtable
        HttpServletRequest httpRequest = (HttpServletRequest)(parameters.get(CoreConstants.GENERAL_ATTR_HTTPREQUEST));
        String viewOutput = null;

        // -----------------------------------------------
        // Security
        // -----------------------------------------------
        if (SecurityManager.isAuthorized(parameters, accountId, sessionId, CoreConstants.RUNTIME_CMND_REQUESTINFO)) {

            // We only want to touch a user's session if the user is logged in
            AuthResponse authResponse = SecurityManager.touch(sessionId, accountId);

            if (!authResponse.isSuccess()) {
                viewOutput =
                        sendErrorToView(VIEW_PACKAGE + viewName, "Error in accessing the " + CoreConstants.RUNTIME_CMND_REQUESTINFO +
                                        " function: " + authResponse.getMessage(), log);
            }
        }

        // -----------------------------------------------
        // Call the model
        // -----------------------------------------------
        Hashtable requestInfo = Runtime.getRequestInfo(httpRequest);

        // -----------------------------------------------
        // Call the view
        // -----------------------------------------------
        viewOutput =
                callViewMethod(VIEW_PACKAGE + viewName, "getRequestInfo", new Object[] { (Object)requestInfo }, log);

        return (viewOutput);
    }

    /**
     * Controller element of the getSessionInfo function.
     *
     * @param viewName The name of the view to render the ouput from the model function
     * @param parameters The parameters for the model function and the security
     * @param accountId The name of the account in which context the function should be executed
     * @param sessionId The session id of the client
     *
     * @return Formatted output of the getSessionInfo
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#getSessionInfo
     */
    public static String getSessionInfo(String viewName, Hashtable parameters, String accountId, String sessionId) {

        // -----------------------------------------------
        // Extract parameters
        // -----------------------------------------------
        // Get the HttpServletRequest containing the parameters from the hashtable
        HttpServletRequest httpRequest = (HttpServletRequest)(parameters.get(CoreConstants.GENERAL_ATTR_HTTPREQUEST));
        String viewOutput = null;

        // -----------------------------------------------
        // Security
        // -----------------------------------------------
        if (!SecurityManager.isAuthorized(parameters, accountId, sessionId, CoreConstants.RUNTIME_CMND_SESSINFO)) {
            viewOutput =
                    sendErrorToView(VIEW_PACKAGE + viewName, "Authorized login required for accessing the " + CoreConstants.RUNTIME_CMND_SESSINFO +
                                    " function", log);
        } else {
            AuthResponse authResponse = SecurityManager.touch(sessionId, accountId);

            if (!authResponse.isSuccess()) {
                viewOutput =
                        sendErrorToView(VIEW_PACKAGE + viewName, "Error in accessing the " + CoreConstants.RUNTIME_CMND_SESSINFO +
                                        " function: " + authResponse.getMessage(), log);
            } else {

                // -----------------------------------------------
                // Call the model
                // -----------------------------------------------
                Hashtable sessionInfo = Runtime.getSessionInfo(httpRequest.getSession(true));

                // -----------------------------------------------
                // Call the view
                // -----------------------------------------------
                viewOutput =
                        callViewMethod(VIEW_PACKAGE + viewName, "getSessionInfo", new Object[] { (Object)sessionInfo },
                                       log);
            }
        }

        return (viewOutput);
    }

    /**
     * Controller element of the doEcho function.
     *
     * @param viewName The name of the view to render the ouput from the model function
     * @param parameters The parameters for the model function and the security
     * @param accountId The name of the account in which context the function should be executed
     * @param sessionId The session id of the client
     *
     * @return Formatted output of the doEcho
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#doEcho
     */
    public static String doEcho(String viewName, Hashtable parameters, String accountId, String sessionId) {

        // -----------------------------------------------
        // Extract parameters
        // -----------------------------------------------
        String echoStringInput = (String)(parameters.get(Constants.ECHO_ATTR_ECHOSTR));
        String xmlWrapping = (String)(parameters.get(Constants.ECHO_ATTR_XMLWRAPPING));
        boolean wrapIfNull = false;
        try {
            if (((String)(parameters.get(Constants.ECHO_ATTR_XMLWRAP_IFNULL))).toLowerCase().equals("true")) {
                wrapIfNull = true;
            }
        } catch (Exception e) {
            wrapIfNull = false;
        }
        String viewOutput = null;

        // -----------------------------------------------
        // Security
        // -----------------------------------------------
        if (!SecurityManager.isAuthorized(parameters, accountId, sessionId, CoreConstants.RUNTIME_CMND_ECHO)) {
            viewOutput =
                    sendErrorToView(VIEW_PACKAGE + viewName, "Authorized login required for accessing the " + CoreConstants.RUNTIME_CMND_ECHO +
                                    " function", log);
        } else {
            AuthResponse authResponse = SecurityManager.touch(sessionId, accountId);

            if (!authResponse.isSuccess()) {
                viewOutput =
                        sendErrorToView(VIEW_PACKAGE + viewName, "Error in accessing the " + CoreConstants.RUNTIME_CMND_ECHO +
                                        " function: " + authResponse.getMessage(), log);
            } else {

                // -----------------------------------------------
                // Call the model
                // -----------------------------------------------
                String echoStringOutput = Runtime.doEcho(echoStringInput, xmlWrapping, wrapIfNull);

                // -----------------------------------------------
                // Call the view
                // -----------------------------------------------
                viewOutput = callViewMethod(VIEW_PACKAGE + viewName, "doEcho", new Object[] { echoStringOutput }, log);
            }
        }

        return (viewOutput);
    }
}
