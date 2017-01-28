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

package edu.stanford.ehs.jml.process.mysql;

/**
 * Constants for the database module
 */
public class Constants {
    public static final String DATABASE_ATTR_PROC_PARAM = "param";
    public static final String DATABASE_ATTR_RESULTNAME = "resultName";
    public static final String DATABASE_ATTR_SQL = "sql";
    public static final String DATABASE_ATTR_STORED_PROCEDURE_NAME = "proc";
    public static final int DATABASE_STTN_MAX_PARAMS = 16;
    public static final String DATABASE_OUTP_METADATA = "meta-data";
    public static final String DATABASE_OUTP_DATABASE_RESULTSET = "result-set";
    public static final String DATABASE_OUTP_COLUMNNAMES = "column-names";
    public static final String DATABASE_OUTP_ORACLE_CACHE_NAME = "oracle_ehsml_cache_";
    public static final String DATABASE_OUTP_SINGLE_VALUE_NAME = "resultValue";
    public static final int DBPING_DEFAULT_FREQUENCY = 30;
    public static final String DATABASE_PING_SQL = "SELECT SYSDATE FROM DUAL";
}
