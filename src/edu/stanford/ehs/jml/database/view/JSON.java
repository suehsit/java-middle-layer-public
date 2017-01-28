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

import edu.stanford.ehs.jml.core.view.JSONTemplate;
import edu.stanford.ehs.jml.database.model.Constants;

import edu.stanford.ehs.jml.util.JSONUtil;
import edu.stanford.ehs.jml.util.XMLUtil;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

/**
 * JSON view for database functions
 */
public class JSON extends JSONTemplate {

    /**
     * JSON formatting for the doUserStoredProcedure model function
     *
     * @param resultSet Hashtable containg the model output
     * @return JSON formatted doUserStoredProcedure
     * @throws java.sql.SQLException
     */
    public static String doUserStoredProcedure(Hashtable resultSet) throws Exception {
        return (doStoredProcedure(resultSet));
    }

    /**
     * JSON formatting for the doStoredProcedure model function
     *
     * @param resultSet Hashtable containg the model output
     * @return JSON formatted doStoredProcedure
     * @throws java.sql.SQLException
     */
    public static String doStoredProcedure(Hashtable resultSet) throws Exception {
        StringBuffer outputView = new StringBuffer();
        addHeader(outputView);
        int totalRowCount = 0;

        // Browse through the results
        Enumeration resultSetDataEnumKeys = resultSet.keys();
        int elementCounter = 0;

        while (resultSetDataEnumKeys.hasMoreElements()) {

            String rowKey = (String)resultSetDataEnumKeys.nextElement(); // the the row name
            Vector rowSet = (Vector)resultSet.get(rowKey); // get the set of rows

            if (elementCounter > 0)
                outputView.append(",");
            outputView.append("\"");
            outputView.append(rowKey);
            outputView.append("-result\":{" + "\"" + rowKey + "\":[");

            Enumeration rows = rowSet.elements();
            int rowCounter = 0;
            while (rows.hasMoreElements()) {
                Hashtable rowAttributes = (Hashtable)rows.nextElement();
                Enumeration rowAttributeEnumKeys = rowAttributes.keys();
                if (rowCounter > 0)
                    outputView.append(",");
                outputView.append("{\"id\":\"");
                outputView.append(rowCounter++);
                outputView.append("\"");
                while (rowAttributeEnumKeys.hasMoreElements()) {
                    String attributeName = (String)rowAttributeEnumKeys.nextElement();
                    String attributeValue = (String)rowAttributes.get(attributeName);

                    outputView.append(",\"");
                    outputView.append(attributeName);
                    outputView.append("\":\"");
                    try {
                        outputView.append(JSONUtil.encode(attributeValue));
                    } catch (Exception e) {
                        outputView.append("&nbsp;" + e.toString());
                    }
                    outputView.append("\"");
                }

                outputView.append("}");
            }
            totalRowCount += rowCounter;
            outputView.append("]}");
            elementCounter++;
        }

        addFooter(outputView);

        log.debug("Number of total rows returned: " + totalRowCount);

        return (outputView.toString());
    }

    /**
     * JSON formatting for the doStoredProcedure model function
     *
     * @param resultSet Hashtable containg the model output
     * @return JSON formatted doStoredProcedure
     * @throws java.sql.SQLException
     */
    public static String doStoredProcedure_formatted(Hashtable resultSet) throws Exception {
        StringBuffer outputView = new StringBuffer();
        addHeader(outputView);
        int totalRowCount = 0;

        // Browse through the results
        Enumeration resultSetDataEnumKeys = resultSet.keys();
        int elementCounter = 0;

        while (resultSetDataEnumKeys.hasMoreElements()) {

            String rowKey = (String)resultSetDataEnumKeys.nextElement(); // the the row name
            Vector rowSet = (Vector)resultSet.get(rowKey); // get the set of rows

            if (elementCounter > 0)
                outputView.append(",");
            outputView.append("\"");
            outputView.append(rowKey);
            outputView.append("-result\":{" + "\"" + rowKey + "\": [");

            Enumeration rows = rowSet.elements();
            int rowCounter = 0;
            while (rows.hasMoreElements()) {
                Hashtable rowAttributes = (Hashtable)rows.nextElement();
                Enumeration rowAttributeEnumKeys = rowAttributes.keys();
                if (rowCounter > 0)
                    outputView.append(",");
                outputView.append("{\"id\":\"");
                outputView.append(rowCounter++);
                outputView.append("\"");
                while (rowAttributeEnumKeys.hasMoreElements()) {
                    String attributeName = (String)rowAttributeEnumKeys.nextElement();
                    String attributeValue = (String)rowAttributes.get(attributeName);

                    outputView.append(",\"");
                    outputView.append(attributeName);
                    outputView.append("\":\"");
                    try {
                        outputView.append(JSONUtil.encode(attributeValue));
                    } catch (Exception e) {
                        outputView.append("&nbsp;" + e.toString());
                    }
                    outputView.append("\"");
                }

                outputView.append(" }");
            }
            totalRowCount += rowCounter;
            outputView.append("] }");
            elementCounter++;
        }

        addFooter(outputView);

        log.debug("Number of total rows returned: " + totalRowCount);

        return (outputView.toString());
    }

}
