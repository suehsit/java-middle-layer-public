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

import java.util.Properties;

/**
 * SMTP settings for an account.
 */
public class SMTPSettings {
    public static final String TRUE = "true";
    public static final String FALSE = "false";

    private boolean debug = false;
    private String smtpPassword;
    private String smtpServer;
    private String smtpUserName;

    public SMTPSettings() {
    }

    /**
     * Return a string representation of the object.
     *
     * @return String representation of the SMTPSettings object
     */
    public String toString() {
        StringBuffer stringValue = new StringBuffer();

        stringValue.append("MAIL_HOST_TAG = " + Constants.EMAIL_HOST_TAG);
        stringValue.append("; MAIL_SMTP_USER_TAG = " + Constants.EMAIL_SMTP_USER_TAG);
        stringValue.append("; MAIL_SMTP_PASSWORD_TAG = " + Constants.EMAIL_SMTP_PASSWORD_TAG);
        stringValue.append("; MAIL_SMTP_DEBUG = " + Constants.EMAIL_SMTP_DEBUG);
        stringValue.append("; smtpServer = " + smtpServer);
        stringValue.append("; smtpUserName = " + smtpUserName);
        stringValue.append("; smtpPassword = " + smtpPassword);
        stringValue.append("; debug = " + debug);

        return (stringValue.toString());
    }

    /**
     * Return the properties of the object in a Properites object.
     *
     * @return Properties
     */
    public Properties getProperties() {
        Properties properties = new Properties();

        if (this.smtpServer != null) {
            properties.setProperty(Constants.EMAIL_HOST_TAG, this.smtpServer);
        }

        if (this.debug) {
            properties.setProperty(Constants.EMAIL_SMTP_DEBUG, this.TRUE);
        } else {
            properties.setProperty(Constants.EMAIL_SMTP_DEBUG, this.FALSE);
        }

        if (this.smtpPassword != null) {
            properties.setProperty(Constants.EMAIL_SMTP_PASSWORD_TAG, this.smtpPassword);
        }

        if (this.smtpUserName != null) {
            properties.setProperty(Constants.EMAIL_SMTP_USER_TAG, this.smtpUserName);
        }

        return (properties);
    }

    /**
     * Get the SMTP password.
     *
     * @return SMTP password
     */
    public String getSmtpPassword() {
        return smtpPassword;
    }

    /**
     * Get the SMTP server name.
     *
     * @return SMTP server name
     */
    public String getSmtpServer() {
        return smtpServer;
    }

    /**
     * Get the SMTP user name.
     *
     * @return SMTP user name
     */
    public String getSmtpUserName() {
        return smtpUserName;
    }

    /**
     * Get the debug level (true or false)
     *
     * @return Debug level
     */
    public boolean getDebug() {
        return debug;
    }

    /**
     * Set the debug level (true or false)
     *
     * @param debug Debug level
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Set the SMTP password
     *
     * @param smtpPassword The SMTP password
     */
    public void setSmtpPassword(String smtpPassword) {
        this.smtpPassword = smtpPassword;
    }

    /**
     * Set the SMTP server name
     * @param smtpServer The SMTP server name
     */
    public void setSmtpServer(String smtpServer) {
        this.smtpServer = smtpServer;
    }

    /**
     * Set the SMTP user name
     *
     * @param smtpUserName The SMTP user name
     */
    public void setSmtpUserName(String smtpUserName) {
        this.smtpUserName = smtpUserName;
    }
}
