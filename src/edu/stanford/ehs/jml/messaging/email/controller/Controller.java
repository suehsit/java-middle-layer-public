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

package edu.stanford.ehs.jml.messaging.email.controller;

import edu.stanford.ehs.jml.core.controller.ControllerTemplate;
import edu.stanford.ehs.jml.core.model.CoreConstants;
import edu.stanford.ehs.jml.messaging.email.model.Constants;
import edu.stanford.ehs.jml.messaging.email.model.EmailMessage;
import edu.stanford.ehs.jml.messaging.email.model.SimpleEmail;
import edu.stanford.ehs.jml.security.model.AuthResponse;
import edu.stanford.ehs.jml.security.model.SecurityManager;

import java.util.Hashtable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Model controller for the email module
 */
public class Controller extends ControllerTemplate {
    protected static Logger log = LogManager.getLogger(Controller.class.getName());
    protected static String VIEW_PACKAGE = "edu.stanford.ehs.jml.email.view.";

    /**
     * Extract the email parameters, ensure proper security, send the email request off to the model and finally,
     * return the result. There is no view associated with this function except for error messages.
     *
     * @param viewName
     * @param parameters
     * @param accountId
     * @param sessionId
     *
     * @return Formatted output from the send function
     *
     * @see edu.stanford.ehs.jml.messaging.email.model.SimpleEmail#send
     */
    public static String send(String viewName, Hashtable parameters, String accountId, String sessionId) {

        // -----------------------------------------------
        // Extract parameters
        // -----------------------------------------------
        // Get the email parametersfrom the HttpServletRequest
        String emailFrom = (String)parameters.get(Constants.EMAIL_ATTR_FROM);
        String emailTo = (String)parameters.get(Constants.EMAIL_ATTR_TO);
        String emailCc = (String)parameters.get(Constants.EMAIL_ATTR_CC);
        String emailBcc = (String)parameters.get(Constants.EMAIL_ATTR_BCC);
        String emailSubject = (String)parameters.get(Constants.EMAIL_ATTR_SUBJECT);
        String emailBody = (String)parameters.get(Constants.EMAIL_ATTR_BODY);
        String viewOutput = null;

        // -----------------------------------------------
        // Security
        // -----------------------------------------------
        if (!SecurityManager.isAuthorized(parameters, accountId, sessionId, CoreConstants.EMAIL_CMND_EMAIL_EMAIL)) {
            viewOutput =
                    sendErrorToView(VIEW_PACKAGE + viewName, "Authorized login required for accessing the " + CoreConstants.EMAIL_CMND_EMAIL_EMAIL +
                                    " function", log);
        } else {
            AuthResponse authResponse = SecurityManager.touch(sessionId, accountId);

            if (!authResponse.isSuccess()) {
                log.error("Error in accessing the " + CoreConstants.EMAIL_CMND_EMAIL_EMAIL + " function: " +
                          authResponse.getMessage());
                viewOutput =
                        sendErrorToView(VIEW_PACKAGE + viewName, "Error in accessing the " + CoreConstants.EMAIL_CMND_EMAIL_EMAIL +
                                        " function: " + authResponse.getMessage(), log);
            } else {

                // -----------------------------------------------
                // Call the model
                // -----------------------------------------------
                EmailMessage emailMessage = new EmailMessage();

                emailMessage.setFrom(emailFrom);
                emailMessage.setTo(emailTo);
                emailMessage.setCc(emailCc);
                emailMessage.setBcc(emailBcc);
                emailMessage.setSubject(emailSubject);
                emailMessage.setBody(emailBody);

                try {
                    SimpleEmail.send(accountId, emailMessage);
                } catch (Exception e) {
                    viewOutput =
                            sendErrorToView(VIEW_PACKAGE + viewName, "Error in invoking the method mail from " + viewName +
                                            ": " + e.toString(), log);
                }

                // -----------------------------------------------
                // Call the view
                // -----------------------------------------------
                // No view call
            }
        }

        return (viewOutput);
    }
}
