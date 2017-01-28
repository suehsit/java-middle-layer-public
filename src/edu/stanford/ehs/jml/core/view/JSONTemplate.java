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

package edu.stanford.ehs.jml.core.view;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JSON view for admin functions
 */
public class JSONTemplate {
    protected static Logger log = LogManager.getLogger(JSONTemplate.class.getName());

    /**
     * Generate an JSON formatted header
     *
     * @param s The StringBuffer for adding the header
     */
    protected static void addHeader(StringBuffer s) {
        s.append("{");
    }

    /**
     * Generate an XML formatted footer
     *
     * @param s The StringBuffer for adding the header
     */
    protected static void addFooter(StringBuffer s) {
        s.append("}");
    }

    public static String actionNotFound(String action) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("\"messages\":{\"msg module\":\"edu.stanford.jml.core.view.JSONTemplate.processRequest\", \"text\":\"Could not process action = ");
        outputView.append(action);
        outputView.append("\"}");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * Generate JSON output for error messages
     *
     * @param errorMessage The string containing the error message
     * @return JSON formatted String with error message
     */
    public static String errorMessage(String errorMessage) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("\"messages\":{\"msg\":{\"module\":\"JSON\", \"id\":\"1\", \"text\":\"Error: ");
        outputView.append(errorMessage);
        outputView.append("\" } }");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * Return the content type for JSON
     *
     * @return String containing the content type (application/json)
     */
    public static String getContentType() {
        return ("application/json");
    }
}
