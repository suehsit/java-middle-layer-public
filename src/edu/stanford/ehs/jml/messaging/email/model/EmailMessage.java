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
 * Email object including basic transport information.
 */
public class EmailMessage {
    private String bcc;
    private String body;
    private String cc;
    private String from;
    private String subject;
    private String to;

    public EmailMessage() {
    }

    /**
     * Return a string representation of the email object.
     *
     * @return String representation of the email object
     */
    public String toString() {
        StringBuffer stringValue = new StringBuffer();

        stringValue.append("Subject=" + subject);
        stringValue.append("; From=" + from);
        stringValue.append("; To=" + to);
        stringValue.append("; CC=" + cc);
        stringValue.append("; BCC=" + bcc);
        stringValue.append("; Body Text=" + body);

        return (stringValue.toString());
    }

    /**
     * Get the blind cc field.
     *
     * @return Bcc field
     */
    public String getBcc() {
        return bcc;
    }

    /**
     * Get the body text.
     *
     * @return Body text
     */
    public String getBody() {
        return body;
    }

    /**
     * Get the cc field.
     *
     * @return Cc field
     */
    public String getCc() {
        return cc;
    }

    /**
     * Get the sender's email address.
     *
     * @return From field
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get the subject of the email.
     *
     * @return Subject field
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get the recipient(s)'s email address(es).
     *
     * @return To field
     */
    public String getTo() {
        return to;
    }

    /**
     * Set the value of the blind CC field.
     *
     * @param bcc Bcc field
     */
    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    /**
     * Set the value of the body text.
     *
     * @param body The body text
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Set the value of the Cc field.
     *
     * @param cc The Cc value
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * Set the sender's email address.
     *
     * @param from Sender's email address
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Set the subject of the email.
     *
     * @param subject The subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Set the recipient(s) of the email.
     *
     * @param to Recipient email address(es)
     */
    public void setTo(String to) {
        this.to = to;
    }
}
