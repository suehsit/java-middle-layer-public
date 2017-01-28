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

import edu.stanford.ehs.jml.security.model.SecurityManager;

import java.io.IOException;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Simple email sender
 */
public class SimpleEmail {
    protected static Logger log = LogManager.getLogger(SimpleEmail.class.getName());

    /**
     * Send an email.
     *
     * @param id The account id
     * @param emailMessage The email message
     * @throws IOException
     * @throws MessagingException
     * @throws Exception
     */
    public static void send(String id, EmailMessage emailMessage) throws IOException, MessagingException, Exception {

        // Prepare the the SMTP settings
        SMTPSettings smtpSettings = SecurityManager.getAccount(id).getSMTPSettings();
        Properties mailProperties = smtpSettings.getProperties();

        log.debug(emailMessage.toString());

        // Create the mail session
        Session mailSession = Session.getInstance(mailProperties, null);

        mailSession.setDebug(smtpSettings.getDebug());

        // Create the message
        Message mimemessage = new MimeMessage(mailSession);

        mimemessage.setFrom(new InternetAddress(emailMessage.getFrom())); // set FROM
        mimemessage.setSentDate(new java.util.Date()); // set DATE

        if (emailMessage.getSubject() != null) {
            mimemessage.setSubject(emailMessage.getSubject()); // set SUBJECT
        }

        mimemessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailMessage.getTo(), false));

        if (emailMessage.getCc() != null) {
            mimemessage.setRecipients(Message.RecipientType.CC, InternetAddress.parse(emailMessage.getCc(), false));
        }

        if (emailMessage.getBcc() != null) {
            mimemessage.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(emailMessage.getBcc(), false));
        }

        // set message BODY
        if (emailMessage.getBody() != null) {
            mimemessage.setText(emailMessage.getBody());
        }

        try {
            // send MAIL
            Transport.send(mimemessage);
        } catch (Exception e) {
            log.error("\tError in sending email: " + e.getMessage());
        }
    }
}
