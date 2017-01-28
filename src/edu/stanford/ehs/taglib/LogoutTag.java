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

import java.util.Hashtable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The LogoutTag implements the jml:logout tag which logs the user out from the
 * specified JML session.
 */
public class LogoutTag extends TagSupport {
    @SuppressWarnings("compatibility:1376297232215008271")
    private static final long serialVersionUID = 1L;

    private String account;

    /**
     * Tag implementation of the logout tag.
     *
     * @return EVAL_BODY_INCLUDE
     * @throws JspException
     *
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {

        System.out.println("Logging out -- 00");

        // Initialize the JSP adapter
        JspAdapter jspAdapter = new JspAdapter();
        System.out.println("Logging out -- 01");
        Hashtable<String, Object> jspParameters = new Hashtable<String, Object>(5);
        System.out.println("Logging out -- 02");
        jspParameters.put(CoreConstants.GENERAL_ATTR_ACCOUNT_ID, account); //// HERE!!!
        System.out.println("Logging out -- 03");
        jspParameters.put(CoreConstants.GENERAL_ATTR_HTTPREQUEST, pageContext.getRequest());
        System.out.println("Logging out -- 04");
        jspParameters.put(CoreConstants.GENERAL_ATTR_ACTION, CoreConstants.SECURITY_CMND_SECURITY_LOGOUT);
        System.out.println("Logging out -- 1");
        // Send the request to the JSP adapter. We are not returning any result
        jspAdapter.processRequest(jspParameters);
        System.out.println("Logging out -- 2");

        return EVAL_BODY_INCLUDE;
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
