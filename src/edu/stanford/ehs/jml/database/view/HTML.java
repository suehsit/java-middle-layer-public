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

package edu.stanford.ehs.jml.database.view;

import edu.stanford.ehs.jml.core.view.HTMLTemplate;
import edu.stanford.ehs.jml.database.model.Constants;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * HTML view for database functions
 */
public class HTML extends HTMLTemplate {

    /**
     * HTML formatting for the doUserStoredProcedure model function
     *
     * @param resultSet Hashtable containg the model output
     * @return XML formatted doUserStoredProcedure Procedure
     * @throws java.sql.SQLException
     */
    public static String doUserStoredProcedure(Hashtable resultSet) throws Exception {
        return (doStoredProcedure(resultSet));
    }

    /**
     * HTML formatting for the doStoredProcedure model function
     *
     * @param resultSet Hashtable containg the model output
     * @return XML formatted doStoredProcedure Procedure
     * @throws java.sql.SQLException
     */
    public static String doStoredProcedure(Hashtable resultSet) throws Exception {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        // Browse through the results
        Enumeration resultSetDataEnumKeys = resultSet.keys();
        while (resultSetDataEnumKeys.hasMoreElements()) {

            String rowKey = (String)resultSetDataEnumKeys.nextElement(); // the the row name
            Vector rowSet = (Vector)resultSet.get(rowKey); // get the set of rows

            outputView.append("\n\t<table border=\"1\">\n\t<h2>");
            outputView.append(rowKey);
            outputView.append("</h2>");

            Enumeration rows = rowSet.elements();

            if (rows.hasMoreElements()) {
                outputView.append("\n\t\t<tr>");
                Hashtable rowAttributes = (Hashtable)rows.nextElement();
                Enumeration rowAttributeEnumKeys = rowAttributes.keys();

                while (rowAttributeEnumKeys.hasMoreElements()) {

                    outputView.append("\n\t\t\t<th align=\"left\">");
                    outputView.append((String)rowAttributeEnumKeys.nextElement());
                    outputView.append("</th>");
                }

                outputView.append("\n\t\t</tr>");
                rows = rowSet.elements(); // reset elements
            }

            // Print the header
            outputView.append("\n\t\t<tr>");

            while (rows.hasMoreElements()) {

                Hashtable rowAttributes = (Hashtable)rows.nextElement();
                Enumeration rowAttributeEnumKeys = rowAttributes.keys();

                while (rowAttributeEnumKeys.hasMoreElements()) {
                    String attributeName = (String)rowAttributeEnumKeys.nextElement();
                    String attributeValue = (String)rowAttributes.get(attributeName);
                    outputView.append("\n\t\t\t<td align=\"left\">");

                    try {
                        outputView.append(attributeValue.trim());
                    } catch (Exception e) {
                        outputView.append("&nbsp;");
                    }
                    outputView.append("</td>");
                }
                outputView.append("\n\t\t</tr>");

            }
            outputView.append("\n\t</table><br/>");
        }
        addFooter(outputView);

        return (outputView.toString());
    }
}
