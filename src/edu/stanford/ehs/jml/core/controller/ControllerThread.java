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

package edu.stanford.ehs.jml.core.controller;

import edu.stanford.ehs.jml.core.model.CoreConstants;
import edu.stanford.ehs.jml.security.model.SecurityManager;

import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;

/**
 * This class contains the main controller which funnels every request through the main
 * security check. Hereafter it delegates the request to one of the model controllers.
 */
public class ControllerThread extends ControllerTemplate {
    protected static Logger log = LogManager.getLogger(ControllerThread.class.getName());
    protected static String VIEW_PACKAGE = "edu.stanford.ehs.jml.core.view.";

    protected static String process(Hashtable controllerInput) {

        // -----------------------------------------------
        // Extract controllerInput
        // -----------------------------------------------
        // Get the HttpServletRequest containing the controllerInput from the hashtable
        HttpServletRequest httpRequest =
            (HttpServletRequest)(controllerInput.get(CoreConstants.GENERAL_ATTR_HTTPREQUEST));

        // Get the action from the HttpServletRequest
        String actionCommand = (String)controllerInput.get(CoreConstants.GENERAL_ATTR_ACTION);

        // Get the view from the controllerInput since it is specific to the adapter
        String viewName = (String)controllerInput.get(CoreConstants.GENERAL_ATTR_VIEW);

        // Get the session id
        String sessionId = SecurityManager.getSessionId(controllerInput);
        
        // Add the session ID to the thread context so it can be included in the application log output
        ThreadContext.put("sessionId", sessionId);

        // Get the account Id from the login table, or, from the HttpServletRequest
        // if the user is logging in seamlessly
        String accountId = edu.stanford.ehs.jml.security.model.SecurityManager.getAccountName(sessionId);
        if (accountId == null) {
            accountId = (String)controllerInput.get(CoreConstants.GENERAL_ATTR_ACCOUNT_ID);
            log.debug("Retrieving account name from parameter: " + accountId);
        }
        String viewOutput = null;

        long rt_freeMemory_kb = 0;
        long rt_totalMemory_kb = 0;
        long rt_maxMemory_kb = 0;
        double rt_freeMemory_perc = 0;

        log.info("User: " + edu.stanford.ehs.jml.security.model.SecurityManager.getUserId(sessionId) + " Action: " +
                 actionCommand + " Account: " + accountId);

        if (log.isDebugEnabled()) {

            rt_freeMemory_kb = Runtime.getRuntime().freeMemory() / 1024;
            rt_totalMemory_kb = Runtime.getRuntime().totalMemory() / 1024;
            rt_maxMemory_kb = Runtime.getRuntime().maxMemory() / 1024;
            rt_freeMemory_perc =
                    (double)((((double)(rt_freeMemory_kb)) / ((double)(rt_totalMemory_kb)) * 10000)) / 100;

            log.debug("Session: " + sessionId);
            log.debug("Remote IP address : " + httpRequest.getRemoteAddr());
            log.debug("Avail mem: " + rt_freeMemory_kb + " kb (" + rt_freeMemory_perc + "%)");
            log.debug("Used mem : " + (rt_totalMemory_kb - rt_freeMemory_kb) + " kb");
            log.debug("Total mem: " + rt_totalMemory_kb + " kb");
            log.debug("Max mem  : " + rt_maxMemory_kb + " kb");
            log.debug("Request URI : " + httpRequest.getRequestURI());
        }

        // --------------------------------------
        // Restrict commands request in open mode
        // --------------------------------------
        String securityCheckResult =
            SecurityManager.securityCheckPoint(actionCommand, sessionId, httpRequest.getRemoteAddr(),
                                               httpRequest.getRequestURI());
        if (securityCheckResult != null) {
            log.info(securityCheckResult);
        }

        // ----------------------------------
        // Main controller delegation
        // ----------------------------------

        if (actionCommand.equals(CoreConstants.DATABASE_CMND_USERSTOREDPROCEDURE)) {
            viewOutput =
                    edu.stanford.ehs.jml.database.controller.Controller.doUserStoredProcedure(viewName, controllerInput,
                                                                                              accountId, sessionId);
        } else if (actionCommand.equals(CoreConstants.SECURITY_CMND_LOGIN)) {
            viewOutput =
                    edu.stanford.ehs.jml.security.controller.Controller.login(viewName, controllerInput, sessionId);
        } else if (actionCommand.equals(CoreConstants.DATABASE_CMND_STOREDPROCEDURE)) {
            viewOutput =
                    edu.stanford.ehs.jml.database.controller.Controller.doStoredProcedure(viewName, controllerInput,
                                                                                          accountId, sessionId);
        } else if (actionCommand.equals(CoreConstants.SECURITY_CMND_SECURITY_LOGOUT)) {
            viewOutput = edu.stanford.ehs.jml.security.controller.Controller.logout(viewName, sessionId);
        } else if (actionCommand.equals(CoreConstants.RUNTIME_CMND_APPRESET)) {
            viewOutput =
                    edu.stanford.ehs.jml.runtime.controller.Controller.resetSession(viewName, controllerInput, accountId,
                                                                                    sessionId);
        } else if (actionCommand.equals(CoreConstants.EMAIL_CMND_EMAIL_EMAIL)) {
            viewOutput =
                    edu.stanford.ehs.jml.messaging.email.controller.Controller.send(viewName, controllerInput, accountId,
                                                                                    sessionId);
        } else if (actionCommand.equals(CoreConstants.RUNTIME_CMND_REQUESTINFO)) {
            viewOutput =
                    edu.stanford.ehs.jml.runtime.controller.Controller.getRequestInfo(viewName, controllerInput, accountId,
                                                                                      sessionId);
        } else if (actionCommand.equals(CoreConstants.ADMIN_CMND_GET_ACTIVE_USERS)) {
            viewOutput =
                    edu.stanford.ehs.jml.admin.controller.Controller.getActiveUsers(viewName, controllerInput, accountId,
                                                                                    sessionId);
        } else if (actionCommand.equals(CoreConstants.ADMIN_CMND_LOGOUTUSER)) {
            viewOutput =
                    edu.stanford.ehs.jml.admin.controller.Controller.logout(viewName, controllerInput, accountId, sessionId);
        } else if (actionCommand.equals(CoreConstants.RUNTIME_CMND_ECHO)) {
            viewOutput =
                    edu.stanford.ehs.jml.runtime.controller.Controller.doEcho(viewName, controllerInput, accountId,
                                                                              sessionId);
        } else if (actionCommand.equals(CoreConstants.RUNTIME_CMND_SESSINFO)) {
            viewOutput =
                    edu.stanford.ehs.jml.runtime.controller.Controller.getSessionInfo(viewName, controllerInput, accountId,
                                                                                      sessionId);
        } else {
            // ----------------------------------
            // Action: Command not found
            // ----------------------------------
            // Call the model
            // Do nothing
            // `null` will be returned
            log.debug(callViewMethod(VIEW_PACKAGE + viewName + "Template", "actionNotFound", new Object[] { actionCommand },
                                   log));
        }


        // Do garbage collection
        Runtime.getRuntime().gc();

        if (log.isDebugEnabled()) {
            long rt_freeMemory_kb_after = Runtime.getRuntime().freeMemory() / 1024;
            long rt_totalMemory_kb_after = Runtime.getRuntime().totalMemory() / 1024;
            log.debug("Mem used(+)/gain(-) by action: " +
                      (long)((rt_totalMemory_kb_after - rt_freeMemory_kb_after) - (rt_totalMemory_kb -
                                                                                   rt_freeMemory_kb)) + " kb");
            log.debug("Mem Alloc inc(+)/dcr(-): " + (long)((rt_totalMemory_kb_after) - (rt_totalMemory_kb)) + " kb");
            log.debug("Used mem after gc: " + (rt_totalMemory_kb_after - rt_freeMemory_kb_after) + " kb");
        }

        return (viewOutput);
    }

    /**
     * Get the content type for a specific view.
     *
     * @param viewName
     * @return The name of the content type of that view
     */
    public static String getContentType(String viewName) {
        return (callViewMethod(VIEW_PACKAGE + viewName + "Template", "getContentType", null, log));
    }

}
