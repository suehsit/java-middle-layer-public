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

import edu.stanford.ehs.jml.database.model.oracle.Query;

/**
 * JSON utilities
 */
public class JSONUtil {

    private static String JSON_CHAR_ESCAPE = "\\\\";
    private static String JSON_CHAR_REVERSE_SOLIDUS = "\\\\";
    private static String JSON_CHAR_QUOTATION = "\"";
    private static String JSON_CHAR_NEWLINE = "\n";
    private static String JSON_CHAR_CARRIAGERETURN = "\r";
    private static String JSON_CHAR_NEWLINE_ESCAPE = "\\\\n";
    private static String JSON_CHAR_CARRIAGERETURN_ESCAPE = "\\\\r";
    private static String JSON_CHAR_INVISIBLE_CONTROL_CHARS = "\\p{C}";

    /**
     * Decode an JSON string
     *
     * @param json String to be decoded
     * @return Decoded JSON string
     */
    public static String decode(String json) {
        if (json != null) {
            json = json.replaceAll(JSON_CHAR_ESCAPE + JSON_CHAR_QUOTATION, JSON_CHAR_QUOTATION);
            json = json.replaceAll(JSON_CHAR_ESCAPE + JSON_CHAR_REVERSE_SOLIDUS, JSON_CHAR_REVERSE_SOLIDUS);
            json = json.replaceAll(JSON_CHAR_ESCAPE + JSON_CHAR_NEWLINE, JSON_CHAR_NEWLINE);
            json = json.replaceAll(JSON_CHAR_ESCAPE + JSON_CHAR_CARRIAGERETURN, JSON_CHAR_CARRIAGERETURN);
            return (json);
        } else {
            return ("");
        }
    }

    /**
     * Encode an JSON string
     *
     * @param json String to be encoded
     * @return Encoded JSON string
     */
    public static String encode(String json) throws Exception {
        if (json != null) {
            json = json.replaceAll(JSON_CHAR_REVERSE_SOLIDUS, JSON_CHAR_ESCAPE + JSON_CHAR_REVERSE_SOLIDUS);
            json = json.replaceAll(JSON_CHAR_QUOTATION, JSON_CHAR_ESCAPE + JSON_CHAR_QUOTATION);
            json = json.replaceAll(JSON_CHAR_NEWLINE, JSON_CHAR_NEWLINE_ESCAPE);
            json = json.replaceAll(JSON_CHAR_CARRIAGERETURN, JSON_CHAR_CARRIAGERETURN_ESCAPE);
            json = json.replaceAll(JSON_CHAR_INVISIBLE_CONTROL_CHARS, "");
            return (json.trim());
        } else {
            return ("");
        }
    }

}
