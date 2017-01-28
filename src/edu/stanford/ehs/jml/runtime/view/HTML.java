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

package edu.stanford.ehs.jml.runtime.view;

import edu.stanford.ehs.jml.core.view.HTMLTemplate;
import edu.stanford.ehs.jml.util.KeyedValue;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * HTML view for the runtime module
 */
public class HTML extends HTMLTemplate {

    /**
     * HTML rendering for the resetSession model function
     *
     * @return HTML string of the resetSession model function
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#resetSession
     */
    public static String resetSession() {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("\n\t\t<table border=1><tr><td>ResetSession() completed</td></tr>");
        outputView.append("\n\t<table>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * HTML rendering for the getRequestInfo model function
     *
     * @return HTML string of the getRequestInfo model function
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#getRequestInfo
     */
    public static String getRequestInfo(Hashtable requestInfo) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        Enumeration requestInfoList = requestInfo.elements();

        outputView.append("\n\t<table border=1><tr><td><b>Name</b></td><td><b>value</b></td><td><b>Description</b></td></tr>");

        while (requestInfoList.hasMoreElements()) {
            KeyedValue requestInfoElement = (KeyedValue)requestInfoList.nextElement();

            outputView.append("\n\t<tr>\n\t\t\t<td>");
            outputView.append(requestInfoElement.getXmlTag());
            outputView.append("</td>\n\t\t\t<td>");
            outputView.append(requestInfoElement.getValue());
            outputView.append("</td>\n\t\t\t<td>");
            outputView.append(requestInfoElement.getDescription());
            outputView.append("</td>\n\t\t</tr>");
        }
        outputView.append("\n\t</table>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * HTML rendering for the doEcho model function
     *
     * @return HTML string of the doEcho model function
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#doEcho
     */
    public static String doEcho(String echoString) {
        return (echoString);
    }

    /**
     * HTML rendering for the getSessionInfo model function
     *
     * @return HTML string of the getSessionInfo model function
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#getSessionInfo
     */
    public static String getSessionInfo(Hashtable sessionInfo) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        outputView.append("\n\t<table border=1><tr><td><b>Description</b></td><td><b>value</b></td></tr>");

        Enumeration sessionInfoList = sessionInfo.elements();

        while (sessionInfoList.hasMoreElements()) {
            KeyedValue sessionInfoElement = (KeyedValue)sessionInfoList.nextElement();

            outputView.append("\n\t<tr>\n\t\t\t<td>");
            outputView.append(sessionInfoElement.getDescription());
            outputView.append("</td>\n\t\t\t<td>");
            outputView.append(sessionInfoElement.getValue());
            outputView.append("</td>\n\t\t</tr>");
        }
        outputView.append("\n\t</table>");

        addFooter(outputView);

        return (outputView.toString());
    }
}
