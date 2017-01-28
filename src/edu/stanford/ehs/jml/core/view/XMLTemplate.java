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
 * XML view for admin functions
 */
public class XMLTemplate {
    protected static Logger log = LogManager.getLogger(XMLTemplate.class.getName());

    /**
     * Generate an XML formatted header
     *
     * @param s The StringBuffer for adding the header
     */
    protected static void addHeader(StringBuffer s) {
        s.append("<?xml version='1.0' encoding='windows-1252'?>\n");
        s.append("<!-- Generator: Environmental Health and Safety Department, Stanford -->\n");

        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
        DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.DEFAULT);
        Date today = new Date();

        s.append("<!-- Date and Time: ");
        s.append(dateFormatter.format(today));
        s.append(" ");
        s.append(timeFormatter.format(today));
        s.append(" -->");
    }

    /**
     * Generate an XML formatted footer
     *
     * @param s The StringBuffer for adding the header
     */
    protected static void addFooter(StringBuffer s) {
    }

    public static String actionNotFound(String action) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("<messages><msg module='edu.stanford.jml.core.view.XMLTemplate.processRequest' text='Could not process action = ");
        outputView.append(action);
        outputView.append("'/></messages>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * Generate XML output for error messages
     *
     * @param errorMessage The string containing the error message
     * @return outputView formatted String with error message
     */
    public static String errorMessage(String errorMessage) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("\n<messages><msg module='XML' id='1' text='Error: ");
        outputView.append(errorMessage);
        outputView.append("'/></messages>");
        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * Return the content type for XML
     *
     * @return String containing the content type (application/xml)
     */
    public static String getContentType() {
        return ("application/xml");
    }
}
