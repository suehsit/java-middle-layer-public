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

import java.util.Enumeration;
import java.util.Hashtable;

/**
 * Hashtable utilities
 */
public class HashtableUtil {

    /**
     * Return a string representation of a simple hashtable (String, String)
     *
     * @param hashtable Hashtable
     *
     * @return String representation of that hashtable
     */
    public static String toString(Hashtable hashtable) {

        Enumeration hashtableKeys = hashtable.keys();
        StringBuffer output = new StringBuffer();
        String hashtableKey = new String();
        String hashtableValueClassName = new String();

        output.append("The hashtable has ");
        output.append(hashtable.size());
        output.append(" keys and values:\n");

        while (hashtableKeys.hasMoreElements()) {
            hashtableKey = (String)hashtableKeys.nextElement();
            hashtableValueClassName = hashtable.get(hashtableKey).getClass().getCanonicalName();
            output.append(hashtableKey);
            output.append(" (");
            output.append(hashtableValueClassName);
            output.append(") ");
            if (hashtableValueClassName.equals("java.lang.String")) {
                output.append("= ");
                output.append((String)hashtable.get(hashtableKey));
            }
            output.append("\n");

        }
        return (output.toString());

    }
}
