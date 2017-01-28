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

/**
 * This helper class contains the getters and setters for a tupple with three and only three values to be
 * used in XML parsing: the XML tag, its description and object value.
 */
public class KeyedValue {
    String description;
    Object value;
    String xmlTag;

    /**
     * The constructor initialized the tupple by specifying its values.
     *
     * @param xmlTag
     * @param description
     * @param value
     */
    public KeyedValue(String xmlTag, String description, Object value) {
        this.xmlTag = xmlTag;
        this.description = description;
        this.value = value;
    }

    /**
     * Get the description of the XML tag.
     *
     * @return Description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the value of the XML tag.
     *
     * @return Object value
     */
    public Object getValue() {
        return value;
    }

    /**
     * Get the XML tag.
     *
     * @return XML tag
     */
    public String getXmlTag() {
        return xmlTag;
    }

    /**
     * Set the description of the XML tag.
     *
     * @param description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set the object value of the XML tag.
     *
     * @param value
     */
    public void setValue(Object value) {
        this.value = value;
    }

    /**
     * Set the XML tag name of the XML tag
     *
     * @param xmlTag
     */
    public void setXmlTag(String xmlTag) {
        this.xmlTag = xmlTag;
    }
}
