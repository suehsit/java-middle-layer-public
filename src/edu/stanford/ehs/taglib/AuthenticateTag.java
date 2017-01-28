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

import java.io.IOException;

import java.util.Hashtable;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The AuthenticateTag implements the jml:authenticate tag which ensures that
 * the user is authenticated to the requested page. The user is not
 * authenticated if one of the following conditions are met:
 * <ul>
 * <li>The user is not logged in to the application using the jml:login tag</li>
 * <li>The user's session with the JSP container has ended</li>
 * <li>The user's session with the JML server has ended</li>
 * </ul>
 * In case of the above, the user will be forwarded to a specified login page.
 */
public class AuthenticateTag extends TagSupport {

    @SuppressWarnings("compatibility:6826966033760191497")
    private static final long serialVersionUID = 1L;
    private String loginPage;
    private String JSPVALIDATE = "jspValidation";
    private String account;

    /**
     * Tag implementation of the authenticate tag. The method probes the doEcho action on the specified
     * JML server and forwards the user to the specified login page if the doEcho action does not return
     * the string that it sent.
     *
     * @return EVAL_BODY_INCLUDE
     * @throws JspException
     *
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {

        // ---------------------------------------------------------------------
        // Check if the JML session has timed out by using the doEcho
        // command
        // ---------------------------------------------------------------------
        JspAdapter jspAdapter = new JspAdapter();
        Hashtable<String, Object> jspParameters = new Hashtable<String, Object>(5);
        jspParameters.put(CoreConstants.GENERAL_ATTR_ACTION, CoreConstants.RUNTIME_CMND_ECHO);
        jspParameters.put(CoreConstants.GENERAL_ATTR_ACCOUNT_ID, account);
        jspParameters.put(edu.stanford.ehs.jml.runtime.model.Constants.ECHO_ATTR_ECHOSTR, JSPVALIDATE);
        jspParameters.put(CoreConstants.GENERAL_ATTR_HTTPREQUEST, pageContext.getRequest());

        // Send the request to the JSP adapter
        String jspResult = jspAdapter.processRequest(jspParameters);
        System.out.println("HERE --- AUTHENTICATE in --- RESULT: " + jspResult);

        // Generate the return value and save it in the specified page context
        // attribute
        if (jspResult != null) {
            if (jspResult.indexOf("Error: Authorized login") > 0) {
                System.out.println("Forwarding the user to " + this.loginPage);
                doForward(this.loginPage);
            } else {
                System.out.println("The user is logged in");
            }
        } else {
            System.out.println("jspResult is Null: Forwarding the user to " + this.loginPage);
            doForward(this.loginPage);

        }


        return (EVAL_BODY_INCLUDE);
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
            HttpServletRequest request = (HttpServletRequest)(pageContext.getRequest());

            RequestDispatcher requestDispatcher = request.getRequestDispatcher(forwardPath);
            requestDispatcher.forward(request, response);

        } catch (IOException e) { // The IOException
            throw new JspException("The login was successful but was not able to forward to " + forwardPath +
                                   " due to an IO exception. Error: " + e.toString());
        } catch (ServletException e) { // The ServletException
            throw new JspException("The login was successful but was not able to forward to " + forwardPath +
                                   " due to servlet exception. Error: " + e.toString());
        }
    }

    /**
     * Specify the login page that an user will be forwarded to, if the user is
     * not logged in
     *
     * @param loginPage URL for login page
     */
    public void setLoginPage(String loginPage) {
        this.loginPage = loginPage;
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
