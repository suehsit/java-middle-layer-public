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
public class XML extends XMLTemplate {

    /**
     * XML format for the function getActiveUsers
     *
     * @param activeUsers Hashtable of active users
     * @return HTML formatted list of users
     */
    public static String getActiveUsers(Hashtable activeUsers) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        Enumeration activeUserSessionIds = activeUsers.keys();

        outputView.append("\n<users>");

        while (activeUserSessionIds.hasMoreElements()) {
            String userSessionId = (String)activeUserSessionIds.nextElement();
            Login userLogin = (Login)activeUsers.get(userSessionId);

            outputView.append("\n\t<user session-id='");
            outputView.append(userSessionId);
            outputView.append("'>\n\t\t<user-id>");
            outputView.append(userLogin.getUserId());
            outputView.append("</user-id>\n\t\t<last-accessed>");
            outputView.append(userLogin.getLastAccessed());
            outputView.append("</last-accessed>\n\t\t<class-name>");
            outputView.append(userLogin.getClass().getName());
            outputView.append("</class-name>\n\t\t<time-out>");
            outputView.append(userLogin.getTimeout());
            outputView.append("</time-out>\n\t\t<account>");
            outputView.append(userLogin.getAccount());
            outputView.append("</account>\n\t</user>");
        }

        outputView.append("\n</users>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * XML formatting for the logoutUser function
     *
     * @param sessionId The session id for the user to be logged out
     * @return Formatted XML output
     */
    public static String logoutUser(String sessionId) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("\n<logout>");
        outputView.append(sessionId);
        outputView.append(" was logged out</logout>");
        addFooter(outputView);

        return (outputView.toString());
    }

}
