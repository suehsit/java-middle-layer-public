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

import edu.stanford.ehs.jml.util.KeyedValue;
import edu.stanford.ehs.jml.core.view.XMLTemplate;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * XML view for the runtime module
 */
public class XML extends XMLTemplate {

    /**
     * XML rendering for the resetSession model function
     *
     * @return XML string of the resetSession model function
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#resetSession
     */
    public static String resetSession() {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("\n<messages><msg module='resetSession' id='1' text='Completed'/></messages>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * XML rendering for the doEcho model function
     *
     * @return XML string of the doEcho model function
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#doEcho
     */
    public static String doEcho(String echoString) {
        return (echoString);
    }

    /**
     * XML rendering for the getRequestInfo model function
     *
     * @return XML string of the getRequestInfo model function
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#getRequestInfo
     */
    public static String getRequestInfo(Hashtable requestInfo) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        Enumeration requestInfoList = requestInfo.elements();

        outputView.append("\n<request-info>");

        while (requestInfoList.hasMoreElements()) {
            KeyedValue requestInfoElement = (KeyedValue)requestInfoList.nextElement();

            outputView.append("\n\t<request-item name='");
            outputView.append(requestInfoElement.getXmlTag());
            outputView.append("' value='");
            outputView.append(requestInfoElement.getValue());
            outputView.append("' description='");
            outputView.append(requestInfoElement.getDescription());
            outputView.append("' />");
        }

        outputView.append("\n</request-info>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * XML rendering for the getSessionInfo model function
     *
     * @return XML string of the getSessionInfo model function
     *
     * @see edu.stanford.ehs.jml.runtime.model.Runtime#getSessionInfo
     */
    public static String getSessionInfo(Hashtable sessionInfo) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        Enumeration sessionInfoList = sessionInfo.elements();

        outputView.append("\n<session-info>");

        while (sessionInfoList.hasMoreElements()) {
            KeyedValue sessionInfoElement = (KeyedValue)sessionInfoList.nextElement();

            outputView.append("\n\t<session-item name='");
            outputView.append(sessionInfoElement.getDescription());
            outputView.append("' value='");
            outputView.append(sessionInfoElement.getValue());
            outputView.append("' />");
        }

        outputView.append("\n</session-info>");
        addFooter(outputView);

        return (outputView.toString());
    }
}
