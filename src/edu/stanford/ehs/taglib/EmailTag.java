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

package edu.stanford.ehs.jml.taglib;

import edu.stanford.ehs.jml.core.controller.adapters.JspAdapter;
import edu.stanford.ehs.jml.core.model.CoreConstants;
import edu.stanford.ehs.jml.messaging.email.model.Constants;

import java.util.Hashtable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The EmailTog class implements the jml:email which sends an email message
 */
public class EmailTag extends TagSupport {

    @SuppressWarnings("compatibility:-9179479493765635168")
    private static final long serialVersionUID = 1L;
    private String name = "jml";
    private String to;
    private String from;
    private String cc;
    private String bcc;
    private String subject;
    private String body;
    private String account;

    /**
     * Tag implementation of the email tag.
     *
     * @return EVAL_BODY_INCLUDE
     * @throws JspException
     *
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */

    public int doStartTag() throws JspException {

        // Initialize the JSP adapter
        JspAdapter jspAdapter = new JspAdapter();
        Hashtable<String, Object> jspParameters = new Hashtable<String, Object>(5);
        jspParameters.put(CoreConstants.GENERAL_ATTR_HTTPREQUEST, pageContext.getRequest());
        jspParameters.put(CoreConstants.GENERAL_ATTR_ACTION, CoreConstants.EMAIL_CMND_EMAIL_EMAIL);

        if (account != null) {
            jspParameters.put(CoreConstants.GENERAL_ATTR_ACCOUNT_ID, account);
        }
        if (to != null) {
            jspParameters.put(Constants.EMAIL_ATTR_TO, to);
        }
        if (cc != null) {
            jspParameters.put(Constants.EMAIL_ATTR_CC, cc);
        }
        if (bcc != null) {
            jspParameters.put(Constants.EMAIL_ATTR_BCC, bcc);
        }
        if (subject != null) {
            jspParameters.put(Constants.EMAIL_ATTR_SUBJECT, subject);
        }
        if (body != null) {
            jspParameters.put(Constants.EMAIL_ATTR_BODY, body);
        }
        if (from != null) {
            jspParameters.put(Constants.EMAIL_ATTR_FROM, from);
        }

        // Send the request to the JSP adapter. We are not retrieving any result back from the request
        jspAdapter.processRequest(jspParameters);

        return EVAL_BODY_INCLUDE;
    }

    /**
     * Set the bcc attribute.
     *
     * @param bcc Bcc
     */
    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    /**
     * Set the body attribute.
     *
     * @param body Body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Set the cc attribute.
     *
     * @param cc Cc
     */
    public void setCc(String cc) {
        this.cc = cc;
    }

    /**
     * Set the from attribute.
     *
     * @param from From
     */
    public void setFrom(String from) {
        this.from = from;
    }

    /**
     * Set the name attribute.
     *
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Set the subject attribute.
     *
     * @param subject Subject
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Set the to attribute.
     *
     * @param to To
     */
    public void setTo(String to) {
        this.to = to;
    }

    /**
     * Set the account name.
     *
     * @param account The name of the account
     */
    public void setAccount(String account) {
        this.account = account;
    }

}
