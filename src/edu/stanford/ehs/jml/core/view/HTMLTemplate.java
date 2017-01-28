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

import java.text.DateFormat;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTML view for admin functions
 */
public class HTMLTemplate {
    protected static Logger log = LogManager.getLogger(HTMLTemplate.class.getName());

    /**
     * Generate an HTML formatted header
     *
     * @param s The StringBuffer for adding the header
     */
    protected static void addHeader(StringBuffer s) {
        s.append("<html>\n<head/>\n");
        s.append("<!-- Generator: Environmental Health and Safety Department, Stanford -->\n");

        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        Date today = new Date();

        s.append("<!-- Date and Time: ");
        s.append(dateFormatter.format(today));
        s.append(" ");
        s.append(timeFormatter.format(today));
        s.append(" -->\n<body>");
    }

    /**
     * Generate an HTML formatted footer
     *
     * @param s The StringBuffer for adding the header
     */
    protected static void addFooter(StringBuffer s) {
        s.append("\n</body>\n</html>");
    }

    /**
     * Generate HTML output for the actionNotFound method
     *
     * @param action The name of the action that was not found
     * @return HTML formatted String
     */
    public static String actionNotFound(String action) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("<br /><font color=\"red\"<b>Could not process action = ");
        outputView.append(action);
        outputView.append("</font>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * Generate HTML output for error messages
     *
     * @param errorMessage The string containing the error message
     * @return outputView formatted String with error message
     */
    public static String errorMessage(String errorMessage) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("<br/><font color=\"red\"><b>Error: </b>");
        outputView.append(errorMessage);
        outputView.append("</font>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * Return the content type for HTML
     *
     * @return String containing the content type (application/xml)
     */
    public static String getContentType() {
        return ("text/html");
    }
}
