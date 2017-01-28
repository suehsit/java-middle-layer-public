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

import edu.stanford.ehs.jml.core.view.HTMLTemplate;
import edu.stanford.ehs.jml.security.model.Login;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * HTML view for admin functions
 */
public class HTML extends HTMLTemplate {

    /**
     * HTML format for the function getActiveUsers
     *
     * @param activeUsers Hashtable of active users
     * @return HTML formatted list of users
     */
    public static String getActiveUsers(Hashtable activeUsers) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        Enumeration activeUserSessionIds = activeUsers.keys();

        outputView.append("\n\t<table border=1><tr><td><b>Session ID</b></td><td><b>User ID</b></td><td><b>Last Accessed</b></td><td><b>Timeout (ms)</b></td><td><b>Account</b></td><td><b>Login Class</b></td></tr>");

        while (activeUserSessionIds.hasMoreElements()) {
            String userSessionId = (String)activeUserSessionIds.nextElement();
            Login userLogin = (Login)activeUsers.get(userSessionId);

            outputView.append("\n\t<tr>\n\t\t\t<td nowrap>");
            outputView.append(userSessionId);
            outputView.append("</td>\n\t\t\t<td nowrap>");
            outputView.append(userLogin.getUserId());
            outputView.append("</td>\n\t\t\t<td nowrap>");
            outputView.append(userLogin.getLastAccessed());
            outputView.append("</td>\n\t\t\t<td nowrap>");
            outputView.append(userLogin.getTimeout());
            outputView.append("</td>\n\t\t\t<td nowrap>");
            outputView.append(userLogin.getAccount());
            outputView.append("</td>\n\t\t\t<td nowrap>");
            outputView.append(userLogin.getClass());
            outputView.append("</td>");
        }

        outputView.append("</td></tr></table>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * HTML formatting for the logoutUser function
     *
     * @param sessionId The session id for the user to be logged out
     * @return Formatted HTML output
     */
    public static String logoutUser(String sessionId) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("\n\t\t<table border=1><tr><td>User with session id ");
        outputView.append(sessionId);
        outputView.append("was logged out</td></tr>");
        outputView.append("\n\t</table>");
        addFooter(outputView);

        return (outputView.toString());
    }

}
