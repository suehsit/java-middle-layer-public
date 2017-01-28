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

package edu.stanford.ehs.jml.runtime.model;

import edu.stanford.ehs.jml.util.KeyedValue;

import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Model functions for the runtime module
 */
public class Runtime {
    protected static Logger log = LogManager.getLogger(Runtime.class.getName());

    /**
     * Reset an HTTP session by removing all attributes from the session, invalidate the session
     * and finally assigning a null value to the session.
     *
     * @param session HTTP session to be reset
     */
    public static void resetSession(HttpSession session) {
        Enumeration e = session.getAttributeNames();

        while (e.hasMoreElements()) {
            session.removeAttribute((String)e.nextElement());
        }

        session.invalidate();
        session = null;
    }

    /**
     * Return the application server's current date and time.
     *
     * @return Current time and date in a Date object
     */
    public static Date getAppServerDateTime() {
        java.util.Date serverDateTime = new java.sql.Timestamp(System.currentTimeMillis());
        return (serverDateTime);
    }

    /**
     * Get the HTTP request's parameters and attributes.
     *
     * @param request
     * @return HTTP request parameters and attributes
     */
    public static Hashtable getRequestInfo(HttpServletRequest request) {
        Hashtable<String, KeyedValue> requestInfo =
            new Hashtable<String, KeyedValue>(20); // Estimated initial capacity 20

        // Get parameters
        Enumeration requestParameterNames = request.getParameterNames();

        while (requestParameterNames.hasMoreElements()) {
            String requestParameterName = (String)requestParameterNames.nextElement();
            String requestParameterValue = request.getParameter(requestParameterName);

            requestInfo.put(requestParameterName,
                            new KeyedValue(requestParameterName, requestParameterName + " (Parameter)",
                                           requestParameterValue));
            log.debug(requestParameterName + " = " + requestParameterValue);
        }

        // Get attributes
        Enumeration requestAttributeNames = request.getAttributeNames();
        String requestAttributeName;
        String requestAttributeValue;
        Object requestAttributeNameObject;
        Object requestAttributeValueObject;
        while (requestAttributeNames.hasMoreElements()) {

            requestAttributeNameObject = requestAttributeNames.nextElement();
            try {
                requestAttributeName = (String)requestAttributeNameObject;
            } catch (ClassCastException e) {
                log.debug("Attempting to cast object name " + requestAttributeNameObject.toString());
                requestAttributeName = ((Integer)requestAttributeNameObject).toString();
            }

            requestAttributeValueObject = request.getAttribute(requestAttributeName);
            try {
                requestAttributeValue = (String)requestAttributeValueObject;
            } catch (ClassCastException e) {
                log.debug("Attempting to cast object value" + requestAttributeValueObject.toString());
                requestAttributeValue = ((Integer)requestAttributeValueObject).toString();
            }


            if (requestAttributeName != null) {
                requestInfo.put(requestAttributeName,
                                new KeyedValue(requestAttributeName, requestAttributeName + " (Attribute)",
                                               requestAttributeValue));
                log.debug(requestAttributeName + " = " + requestAttributeValue);
            }
        }

        // Capture the WebAuth information
        requestInfo.put("WEBAUTH_USER",
                        new KeyedValue("WEBAUTH_USER", "WEBAUTH_USER" + " (Attribute)", (String)request.getAttribute("WEBAUTH_USER")));
        requestInfo.put("REMOTE_USER",
                        new KeyedValue("REMOTE_USER", "REMOTE_USER" + " (Attribute)", (String)request.getAttribute("REMOTE_USER")));
        requestInfo.put("AUTH_TYPE",
                        new KeyedValue("AUTH_TYPE", "AUTH_TYPE" + " (Attribute)", (String)request.getAttribute("AUTH_TYPE")));
        requestInfo.put("WEBAUTH_TOKEN_CREATION",
                        new KeyedValue("WEBAUTH_TOKEN_CREATION", "WEBAUTH_TOKEN_CREATION" + " (Attribute)",
                                       (String)request.getAttribute("WEBAUTH_TOKEN_CREATION")));
        requestInfo.put("WEBAUTH_TOKEN_EXPIRATION",
                        new KeyedValue("WEBAUTH_TOKEN_EXPIRATION", "WEBAUTH_TOKEN_EXPIRATION" + " (Attribute)",
                                       (String)request.getAttribute("WEBAUTH_TOKEN_EXPIRATION")));

        return (requestInfo);
    }

    public static Hashtable getSessionInfo(HttpSession httpSession) {
        Hashtable<String, KeyedValue> sessionInfo =
            new Hashtable<String, KeyedValue>(20); // Estimated initial capacity 20
        long longSessionCreationTime = httpSession.getCreationTime();
        long longSessionLastAccessedTime = httpSession.getLastAccessedTime();
        long longSessionDuration = (longSessionLastAccessedTime - longSessionCreationTime);
        Date dateSessionCreationTime = new Date(longSessionCreationTime);
        Date dateSessionLastAccessedTime = new Date(longSessionLastAccessedTime);

        if (dateSessionLastAccessedTime.before(dateSessionCreationTime)) {
            dateSessionLastAccessedTime = dateSessionCreationTime;
            longSessionDuration = 0;
        }

        sessionInfo.put("reported-by",
                        new KeyedValue("reported-by", "Reported by", "edu.stanford.ehs.ml.model.Runtime->getSessionInfo(HttpSession)"));
        sessionInfo.put("session-id", new KeyedValue("session-id", "Current Session Id", httpSession.getId()));
        sessionInfo.put("session-created",
                        new KeyedValue("session-created", "Session Created Time", dateSessionCreationTime));
        sessionInfo.put("session-accessed",
                        new KeyedValue("session-accessed", "Session Accessed Time", dateSessionLastAccessedTime));
        sessionInfo.put("session-duration",
                        new KeyedValue("session-duration", "Session Duration", String.valueOf(longSessionDuration)));

        Integer maxInactiveInterval = new Integer(httpSession.getMaxInactiveInterval());

        sessionInfo.put("session-max-inactive-interval",
                        new KeyedValue("session-max-inactive-interval", "Session Max Inactive Interval Seconds",
                                       maxInactiveInterval.toString()));

        Enumeration sessionAttributeNames = httpSession.getAttributeNames();
        String sessionAttributeKey = null;
        String sessionAttributeValue = null;

        while (sessionAttributeNames.hasMoreElements()) {
            try {
                sessionAttributeKey = (String)sessionAttributeNames.nextElement();
                sessionAttributeValue = (String)httpSession.getAttribute(sessionAttributeKey);
            } catch (Exception e) {
                log.debug("Error in parsing session attribute key " + sessionAttributeKey + ": " + e.toString());
                sessionAttributeValue = "*** Key value is not a string ***";
            }

            if (!sessionAttributeKey.equals("x")) {
                sessionInfo.put("session-item-" + sessionAttributeKey,
                                new KeyedValue("session-item-" + sessionAttributeKey, sessionAttributeKey,
                                               sessionAttributeValue));
            }
        }

        return (sessionInfo);
    }

    public static String doEcho(String echoString, String xmlWrapping, boolean wrapIfNull) {

        StringBuffer returnedEchoString = new StringBuffer();

        // Wrap the echo string with xmlWrapping, if the string is an XML String
        if ((xmlWrapping != null) || (wrapIfNull)) {

            String trimmedEchoString = echoString.trim();

            // Examine if the lenght of the echo string can contain a valid XML
            // string. The mimimum lenght is 7: <x></x>
            if (trimmedEchoString.length() >= 7) {

                // Examine the string for a valid starting XML tag. If not, treat
                // the string as if the xmlWrapping has not been specified
                if ((trimmedEchoString.charAt(0) == '<') && ((trimmedEchoString.indexOf('>')) != -1)) {
                    String xmlWrapperTag = trimmedEchoString.substring(1, trimmedEchoString.indexOf('>'));

                    // Add the front tag wrapper
                    returnedEchoString.append('<');
                    returnedEchoString.append(xmlWrapperTag);
                    returnedEchoString.append(xmlWrapping);
                    returnedEchoString.append('>');

                    // Add the content
                    returnedEchoString.append(echoString);

                    // Add the tailing tag wrapper
                    returnedEchoString.append("</");
                    returnedEchoString.append(xmlWrapperTag);
                    returnedEchoString.append(xmlWrapping);
                    returnedEchoString.append('>');

                }
            }
        }

        if (returnedEchoString.toString().length() == 0)
            returnedEchoString = new StringBuffer(echoString);

        return (returnedEchoString.toString());
    }

}
