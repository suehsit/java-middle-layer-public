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

package edu.stanford.ehs.jml.util;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * XML utilities
 */
public class XMLUtil {

    // The XML translation table
    private static String[][] XML_TRANSLATION =
    { { "&", "&amp;" }, { "<", "&lt;" }, { ">", "&gt;" }, { "\"", "&quot;" }, { "'", "&apos;" } };

    private static int XML_TRANSLATION_LENGTH = XML_TRANSLATION.length;
    private static int XML_TRANSLATION_SYMBOL = 0; // Array parameter index of symbol
    private static int XML_TRANSLATION_CODE = 1; // Array parameter index of code

    /**
     * Decode an XML string
     *
     * @param xml String to be decoded
     * @return Decoded XML string
     */
    public static String decode(String xml) {
        if (xml != null) {
            for (int i = 0; i < XML_TRANSLATION.length; i++) {
                xml =
xml.replaceAll(XML_TRANSLATION[i][XML_TRANSLATION_CODE], XML_TRANSLATION[i][XML_TRANSLATION_SYMBOL]);
            }
            return (xml);
        } else {
            return ("");
        }
    }

    /**
     * Encode an XML string
     *
     * @param xml String to be encoded
     * @return Encoded XML string
     */
    public static String encode(String xml) throws Exception {
        if (xml != null) {
            for (int i = 0; i < XML_TRANSLATION_LENGTH; i++) {
                xml =
xml.replaceAll(XML_TRANSLATION[i][XML_TRANSLATION_SYMBOL], XML_TRANSLATION[i][XML_TRANSLATION_CODE]);
            }
            return (xml.trim());
        } else {
            return ("");
        }
    }

    public static int translations() {
        return (XML_TRANSLATION.length);
    }


    /**
     * Retrieve the value of an XML tag. For internal use only during parsing the accounts
     * configuration file.
     *
     * @param element The elemement containing the tag
     * @param tagName The string name of the tag
     * @return Tag value
     */
    public static String getTagValue(Element element, String tagName) throws Exception {
        try {
            NodeList tagList = element.getElementsByTagName(tagName);
            Element tagElement = (Element)tagList.item(0);
            NodeList textTagList = tagElement.getChildNodes();

            return (textTagList.item(0)).getNodeValue().trim();
        } catch (Exception e) {
            throw new Exception("Error in parsing the element \"" + element.toString() + "\" with the tag \"" +
                                tagName + "\"");
        }
    }

    /**
     * Retrieve the attribute value of an XML tag. For internal use only during parsing the accounts
     * configuration file.
     *
     * @param element The elemement containing the tag
     * @param attributeName The attribute name of the tag
     * @return Tag value
     */
    private String getAttributeValue(Element element, String attributeName) {
        return element.getAttribute(attributeName);
    }


}
