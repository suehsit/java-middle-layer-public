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

package edu.stanford.ehs.jml.core.model;

/**
 * Constants for the JML Server.
 */
public class CoreConstants {

    // Version
    public static final String PRODUCT_NAME = "Java Middle Layer";
    public static final String PRODUCT_OWNER = "Environmental Health and Safety, Stanford University";
    public static final String PRODUCT_VERSION = "3.5.0";
    public static final String PRODUCT_DATE = "2017.03.20 13:48";

    // General attributes
    public static final String GENERAL_ATTR_ACTION = "action";
    public static final String GENERAL_ATTR_ACCOUNT_ID = "account";
    public static final String GENERAL_ATTR_SESSION_ID = "jsessid";
    public static final String GENERAL_ATTR_DEBUG = "debug";
    public static final String GENERAL_ATTR_HTTPREQUEST = "httprequest";
    public static final String GENERAL_ATTR_VIEW = "view";
    public static final String GENERAL_ATTR_CSRF_TOKEN = "token";

    //  Commands
    public static final String ADMIN_CMND_LOGOUTUSER = "logoutUser";
    public static final String ADMIN_CMND_GET_ACTIVE_USERS = "getActiveUsers";
    public static final String DATABASE_CMND_STOREDPROCEDURE = "doStoredProcedure";
    public static final String DATABASE_CMND_USERSTOREDPROCEDURE = "doUserStoredProcedure";
    public static final String EMAIL_CMND_EMAIL_EMAIL = "email";
    public static final String GENERAL_CMND_ERRORMESSAGE = "errorMessage";
    public static final String RUNTIME_CMND_APPRESET = "resetApplication";
    public static final String RUNTIME_CMND_ECHO = "doEcho";
    public static final String RUNTIME_CMND_REQUESTINFO = "getRequestInfo";
    public static final String RUNTIME_CMND_SESSINFO = "getSessionInfo";
    public static final String SECURITY_CMND_LOGIN = "login";
    public static final String SECURITY_CMND_SECURITY_LOGOUT = "logout";

    // Caching
    public static final String CACHE_PREFIX = "jmlCache";

}
