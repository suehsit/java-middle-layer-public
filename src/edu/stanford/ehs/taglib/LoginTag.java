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
import edu.stanford.ehs.jml.security.model.Constants;

import java.io.IOException;

import java.util.Hashtable;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

/**
 * The LoginTag implements the jml:login tag which allows a JML to login from a
 * JSP page and create a JML session throughout the user's JSP session.
 */
public class LoginTag extends BodyTagSupport {

    @SuppressWarnings("compatibility:749477481695975524")
    private static final long serialVersionUID = 1L;
    private String user;
    private String password;
    private String account;
    private String loginResult;
    private String onSuccessPage;
    private String INVALID_LOGIN = "false";
    private String OK_LOGIN = "true";
    private String isLoginSuccess = null;

    /**
     * Tag implementation of the login tag.
     *
     * @return EVAL_BODY_INCLUDE
     * @throws JspException
     *
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {

        // Start setting the login result to false
        isLoginSuccess = this.INVALID_LOGIN;

        // Initialize the JSP adapter
        JspAdapter jspAdapter = new JspAdapter();
        Hashtable<String, Object> jspParameters = new Hashtable<String, Object>(5);
        jspParameters.put(CoreConstants.GENERAL_ATTR_ACCOUNT_ID, account);
        jspParameters.put(CoreConstants.GENERAL_ATTR_HTTPREQUEST, pageContext.getRequest());
        jspParameters.put(CoreConstants.GENERAL_ATTR_ACTION, CoreConstants.SECURITY_CMND_LOGIN);
        jspParameters.put(Constants.SECURITY_ATTR_SECURITY_SIMPLE_USERNAME, user);
        jspParameters.put(Constants.SECURITY_ATTR_SECURITY_SIMPLE_PASSWORD, password);
        System.out.println("HERE --- Logging in. user=" + user + " and password=" + password);
        // Send the request to the JSP adapter
        String jspResult = jspAdapter.processRequest(jspParameters);
        System.out.println("HERE --- Logging in --- RESULT: " + jspResult);

        // Generate the return value and save it in the specified page context
        // attribute
        if (jspResult != null) {
            if (jspResult.indexOf("Login was successful") > 0) {
                isLoginSuccess = this.OK_LOGIN;
            }
        }
        pageContext.setAttribute(loginResult, isLoginSuccess);

        return EVAL_BODY_INCLUDE;
    }

    /**
     * Tag implementation of the login tag.
     *
     * @return EVAL_PAGE
     * @throws JspException
     *
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doEndTag() throws JspException {

        // Forward only if onSuccessPage is defined and if the login was
        // successful
        if (isLoginSuccess != null) {
            if (isLoginSuccess.equals(this.OK_LOGIN)) {
                if (onSuccessPage != null)
                    this.doForward(onSuccessPage);
            }
        }
        // Skip the remainder of this page
        return (EVAL_PAGE);
    }

    /**
     * Forwards the login request to the onSuccessPage if the page is specified
     * and if the JML login was successful.
     *
     * @param forwardPath The forward path
     * @throws JspException
     */
    protected void doForward(String forwardPath) throws JspException {
        try {
            HttpServletResponse response = (HttpServletResponse)(pageContext.getResponse());
            response.sendRedirect(forwardPath);
        } catch (IOException e) { // The IOException and ServletException
            throw new JspException("The login was successful but was not able to forward to " + forwardPath +
                                   " due to an IO exception. Error: " + e.toString());
        }
    }

    /**
     * Set the account name.
     *
     * @param account The name of the account
     */
    public void setAccount(String account) {
        this.account = account;
    }

    /**
     * Set the password for the login.
     *
     * @param password The password
     */
    public void setPassword(String password) {
        System.out.println("Setting password to " + password);
        this.password = password;
    }

    /**
     * Set the login result which will either return the String true or false.
     *
     * @param loginResult The result of the login
     */
    public void setLoginResult(String loginResult) {
        this.loginResult = loginResult;
    }

    /**
     * Set the user name for the login.
     *
     * @param user User name
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Set the page that the request will be forwarded to after a successful login.
     *
     * @param onSuccessPage The URL of the on-success page
     */
    public void setOnSuccessPage(String onSuccessPage) {
        this.onSuccessPage = onSuccessPage;
    }

}
