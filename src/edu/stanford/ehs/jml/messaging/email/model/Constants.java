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

package edu.stanford.ehs.jml.messaging.email.model;

/**
 * Constants for the email module
 */
public class Constants {
    public static final String EMAIL_ATTR_TO = "to";
    public static final String EMAIL_ATTR_FROM = "from";
    public static final String EMAIL_ATTR_CC = "cc";
    public static final String EMAIL_ATTR_BCC = "bcc";
    public static final String EMAIL_ATTR_SUBJECT = "subject";
    public static final String EMAIL_ATTR_BODY = "body";
    public static final String EMAIL_ATTR_ATTACHMENTS = "attachements";

    public static final String EMAIL_HOST_TAG = "mail.smtp.host";
    public static final String EMAIL_SMTP_USER_TAG = "mail.smtp.user";
    public static final String EMAIL_SMTP_PASSWORD_TAG = "mail.smtp.password";
    public static final String EMAIL_SMTP_DEBUG = "mail.smtp.debug";
}
