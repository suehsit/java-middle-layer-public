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

package edu.stanford.ehs.jml.admin.view;

import edu.stanford.ehs.jml.core.view.XMLTemplate;
import edu.stanford.ehs.jml.security.model.Login;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * XML view for admin functions
 */
public class JSON extends XMLTemplate {

    /**
     * JSON format for the function getActiveUsers
     *
     * @param activeUsers Hashtable of active users
     * @return JSON formatted list of users
     */
    public static String getActiveUsers(Hashtable activeUsers) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        Enumeration activeUserSessionIds = activeUsers.keys();

        outputView.append("\"users\" : { ");

        while (activeUserSessionIds.hasMoreElements()) {
            String userSessionId = (String)activeUserSessionIds.nextElement();
            Login userLogin = (Login)activeUsers.get(userSessionId);

            outputView.append("\"user\" : { ");
            outputView.append("\"session-id\" : \"");
            outputView.append(userSessionId);
            outputView.append("\", \"user-id\" : \"");
            outputView.append(userLogin.getUserId());
            outputView.append("\", \"last-accessed\" : \"");
            outputView.append(userLogin.getLastAccessed());
            outputView.append("\", \"class-name\" : \"");
            outputView.append(userLogin.getClass().getName());
            outputView.append("\", \"time-out\" : \"");
            outputView.append(userLogin.getTimeout());
            outputView.append("\", \"account\" : \"");
            outputView.append(userLogin.getAccount());
            outputView.append("\"}");
        }

        outputView.append("}");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * JSON formatting for the logoutUser function
     *
     * @param sessionId The session id for the user to be logged out
     * @return Formatted XML output
     */
    public static String logoutUser(String sessionId) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("\"logout\" : \"");
        outputView.append(sessionId);
        outputView.append(" was logged out\"");
        addFooter(outputView);

        return (outputView.toString());
    }

}
