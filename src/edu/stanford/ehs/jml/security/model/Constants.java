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

package edu.stanford.ehs.jml.security.model;

/**
 * Constants for the security module
 */
public class Constants {
    public static final String SECURITY_OPEN_MODE = "open";
    public static final String SECURITY_ATTR_SECURITY_SIMPLE_USERNAME = "username";
    public static final String SECURITY_ATTR_SECURITY_SIMPLE_PASSWORD = "password";
    public static final String SECURITY_CONT_SECURITY_CREDENTIALS = "credentials";
    public static final String SECURITY_SERVLET_ACCOUNTS_FILE = "accounts-file";
    public static final String SECURITY_SERVLET_CONFIG_FILE = "config-file";
    public static final int SECURITY_DEFAULT_FREQUENCY = 60;

    // Security modes
    public static final String SECURITY_MODE_ALLRESTRICTEDACTIONS = "AllRestrictedActions";
    public static final String SECURITY_MODE_ONLYLOCALREQUESTS = "OnlyLocalRequests";
    public static final String SECURITY_MODE_PARTIALRESTRICTEDACTIONS = "PartialRestrictedActions";
}
