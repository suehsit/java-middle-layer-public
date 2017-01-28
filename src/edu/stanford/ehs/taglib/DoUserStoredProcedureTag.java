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
import edu.stanford.ehs.jml.database.model.Constants;

import java.util.Hashtable;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

/**
 * The DoUserStoredProcedureTag class implements the jml:doUserStoredProcedure
 * tag which allows a JML login from a JSP page and create a JML session
 * throughout the user's JSP session.
 */
public class DoUserStoredProcedureTag extends TagSupport {

    @SuppressWarnings("compatibility:-1451950507015973152")
    private static final long serialVersionUID = 1L;
    private String proc;
    private String account;
    private String param1;
    private String param2;
    private String param3;
    private String param4;
    private String param5;
    private String param6;
    private String param7;
    private String param8;
    private String param9;
    private String param10;
    private String param11;
    private String param12;
    private String param13;
    private String param14;
    private String param15;
    private String param16;
    private String var;

    /**
     * Tag implementation of the doUserStoredProcedure tag.
     *
     * @return EVAL_BODY_INCLUDE
     * @throws JspException
     *
     * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
     */
    public int doStartTag() throws JspException {

        // Initialize the JSP adapter
        JspAdapter jspAdapter = new JspAdapter();
        Hashtable<String, Object> jspParameters = new Hashtable<String, Object>(25);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "1", param1);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "2", param2);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "3", param3);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "4", param4);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "5", param5);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "6", param6);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "7", param7);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "8", param8);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "9", param9);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "10", param10);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "11", param11);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "12", param12);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "13", param13);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "14", param14);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "15", param15);
        setParameter(jspParameters, Constants.DATABASE_ATTR_PROC_PARAM + "16", param16);
        setParameter(jspParameters, CoreConstants.GENERAL_ATTR_ACTION,
                     CoreConstants.DATABASE_CMND_USERSTOREDPROCEDURE);
        setParameter(jspParameters, Constants.DATABASE_ATTR_STORED_PROCEDURE_NAME, proc);
        setParameter(jspParameters, CoreConstants.GENERAL_ATTR_ACCOUNT_ID, account);
        setParameter(jspParameters, CoreConstants.GENERAL_ATTR_HTTPREQUEST, pageContext.getRequest());

        // Send the request to the JSP adapter
        String jspResult = jspAdapter.processRequest(jspParameters);

        if (jspResult != null) {
            pageContext.setAttribute(var, jspResult);
        }

        return EVAL_BODY_INCLUDE;
    }

    private void setParameter(Hashtable<String, Object> hs, String k, Object v) throws JspException {
        try {
            if ((k != null) && (v != null)) {
                hs.put(k, v);
            }
        } catch (Exception e) {
            throw new JspException("Was not able to set (" + k + "," + v + ") in jspParameters");
        }
    }

    /**
     * Set the param1 attribute.
     *
     * @param param1 Param1 of the stored procedure
     */
    public void setParam1(String param1) {
        this.param1 = param1;
    }

    /**
     * Set the param10 attribute.
     *
     * @param param10 Param10 of the stored procedure
     */
    public void setParam10(String param10) {
        this.param10 = param10;
    }

    /**
     * Set the param11 attribute.
     *
     * @param param11 Param11 of the stored procedure
     */
    public void setParam11(String param11) {
        this.param11 = param11;
    }

    /**
     * Set the param12 attribute.
     *
     * @param param12 Param12 of the stored procedure
     */
    public void setParam12(String param12) {
        this.param12 = param12;
    }

    /**
     * Set the param13 attribute.
     *
     * @param param13 Param13 of the stored procedure
     */
    public void setParam13(String param13) {
        this.param13 = param13;
    }

    /**
     * Set the param14 attribute.
     *
     * @param param14 Param14 of the stored procedure
     */
    public void setParam14(String param14) {
        this.param14 = param14;
    }

    /**
     * Set the param15 attribute.
     *
     * @param param15 Param15 of the stored procedure
     */
    public void setParam15(String param15) {
        this.param15 = param15;
    }

    /**
     * Set the param16 attribute.
     *
     * @param param16 Param16 of the stored procedure
     */
    public void setParam16(String param16) {
        this.param16 = param16;
    }

    /**
     * Set the param2 attribute.
     *
     * @param param2 Param2 of the stored procedure
     */
    public void setParam2(String param2) {
        this.param2 = param2;
    }

    /**
     * Set the param3 attribute.
     *
     * @param param3 Param3 of the stored procedure
     */
    public void setParam3(String param3) {
        this.param3 = param3;
    }

    /**
     * Set the param4 attribute.
     *
     * @param param4 Param4 of the stored procedure
     */
    public void setParam4(String param4) {
        this.param4 = param4;
    }

    /**
     * Set the param5 attribute.
     *
     * @param param5 Param5 of the stored procedure
     */
    public void setParam5(String param5) {
        this.param5 = param5;
    }

    /**
     * Set the param6 attribute.
     *
     * @param param6 Param6 of the stored procedure
     */
    public void setParam6(String param6) {
        this.param6 = param6;
    }

    /**
     * Set the param7 attribute.
     *
     * @param param7 Param7 of the stored procedure
     */
    public void setParam7(String param7) {
        this.param7 = param7;
    }

    /**
     * Set the param8 attribute.
     *
     * @param param8 Param8 of the stored procedure
     */
    public void setParam8(String param8) {
        this.param8 = param8;
    }

    /**
     * Set the param9 attribute.
     *
     * @param param9 Param9 of the stored procedure
     */
    public void setParam9(String param9) {
        this.param9 = param9;
    }

    /**
     * Set the proc attribute.
     *
     * @param proc Proc of the stored procedure
     */
    public void setProc(String proc) {
        this.proc = proc;
    }

    /**
     * Set the var attribute.
     *
     * @param var Var of the stored procedure
     */
    public void setVar(String var) {
        this.var = var;
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
