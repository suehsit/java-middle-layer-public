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

package edu.stanford.ehs.jml.database.controller;

import edu.stanford.ehs.jml.core.controller.ControllerTemplate;
import edu.stanford.ehs.jml.core.model.CoreConstants;
import edu.stanford.ehs.jml.database.model.Constants;
import edu.stanford.ehs.jml.database.model.oracle.Query;
import edu.stanford.ehs.jml.security.model.AuthResponse;
import edu.stanford.ehs.jml.security.model.SecurityManager;

import java.util.Hashtable;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Model controller for the database module
 */
public class Controller extends ControllerTemplate {
    protected static Logger log = LogManager.getLogger(Controller.class.getName());
    protected static String VIEW_PACKAGE = "edu.stanford.ehs.jml.database.view.";
    private static int INITIAL_PARAMETER_SIZE = 20;

    /**
     * Execute a stored procedure, send the result to the specied view and return the formatted result
     *
     * @param viewName The view that should format the result from the stored procedure
     * @param parameters The hashtable containing the name of the stored procedure and its parameters
     * @param accountId The account that is performing the stored procedure
     * @param sessionId The session id that is requesting the stored procedure
     *
     * @return Formatted query result
     *
     * @see edu.stanford.ehs.jml.database.model.oracle.Query#doStoredProcedure
     */
    public static String doStoredProcedure(String viewName, Hashtable parameters, String accountId, String sessionId) {

        // -----------------------------------------------
        // Extract parameters
        // -----------------------------------------------
        // Get the SQL query from the HttpServletRequest
        String storedProcedureName = (String)parameters.get(Constants.DATABASE_ATTR_STORED_PROCEDURE_NAME);

        // Copy parameters and names for stored procedures and functions
        Vector<String> procParameters = new Vector<String>(INITIAL_PARAMETER_SIZE);

        // Do a qualified guess on the number of parameters passed to the stored
        // procedure; In Query.java we will find the exact number of parameters
        int numberOfParameters = 0;
        for (int i = Constants.DATABASE_STTN_MAX_PARAMS - 1; i >= 0; i--) {
            if ((parameters.get(Constants.DATABASE_ATTR_PROC_PARAM + i) != null) ||
                (parameters.get(Constants.DATABASE_ATTR_RESULTNAME + i) != null)) {
                numberOfParameters = i;
                break;
            }
        }
        log.debug(numberOfParameters + " parameters found");

        // Retrieve the parameter values
        for (int i = 1; i <= numberOfParameters; i++) {
            String procParameterValue = (String)parameters.get(Constants.DATABASE_ATTR_PROC_PARAM + i);
            log.debug("procParameterValue(" + i + "): " + procParameterValue);
            procParameters.add(procParameterValue);
        }
        String viewOutput = null;

        // -----------------------------------------------
        // Security
        // -----------------------------------------------
        if (!SecurityManager.isAuthorized(parameters, accountId, sessionId,
                                          CoreConstants.DATABASE_CMND_STOREDPROCEDURE)) {
            viewOutput =
                    sendErrorToView(VIEW_PACKAGE + viewName, "Authorized login required for accessing the " + CoreConstants.DATABASE_CMND_STOREDPROCEDURE +
                                    " function", log);
        } else {
            AuthResponse authResponse = SecurityManager.touch(sessionId, accountId);

            if (!authResponse.isSuccess()) {
                viewOutput =
                        sendErrorToView(VIEW_PACKAGE + viewName, "Error in accessing the " + CoreConstants.DATABASE_CMND_STOREDPROCEDURE +
                                        " function: " + authResponse.getMessage(), log);
            } else {

                // -----------------------------------------------
                // Call the model
                // -----------------------------------------------
                long timerStartModel = System.currentTimeMillis();
                Hashtable queryResult = null;

                try {
                    queryResult = Query.doStoredProcedure(accountId, storedProcedureName, procParameters);

                } catch (Exception e) {
                    log.error("Error in getting a db connection with the id " + accountId + ": " + e.toString());
                }

                long timerEndModel = System.currentTimeMillis();

                // -----------------------------------------------
                // Call the view
                // -----------------------------------------------
                long timerStartView = System.currentTimeMillis();

                if (queryResult.size() > 0) {
                    viewOutput =
                            callViewMethod(VIEW_PACKAGE + viewName, "doStoredProcedure", new Object[] { (Object)queryResult },
                                           log);
                    queryResult = null; // House cleaning
                } else {
                    log.debug("The result set was null and the view was not called.");
                }

                long timerEndView = System.currentTimeMillis();

                log.info("Model time: " + (timerEndModel - timerStartModel) + " ms");
                log.info("View times: " + (timerEndView - timerStartView) + " ms");
                log.info("Total: " + ((timerEndModel - timerStartModel + timerEndView) - timerStartView) + " ms");
            }
        }
        return (viewOutput);
    }

    /**
     * Execute a stored procedure, send the result to the specied view and return the formatted result
     *
     * @param viewName The view that should format the result from the stored procedure
     * @param parameters The hashtable containing the name of the stored procedure and its parameters
     * @param accountId The account that is performing the stored procedure
     * @param sessionId The session id that is requesting the stored procedure
     *
     * @return Formatted query result
     *
     * @see edu.stanford.ehs.jml.database.model.oracle.Query#doUserStoredProcedure
     */
    public static String doUserStoredProcedure(String viewName, Hashtable parameters, String accountId,
                                               String sessionId) {

        // -----------------------------------------------
        // Extract parameters
        // -----------------------------------------------
        // Get the SQL query from the HttpServletRequest
        String storedProcedureName = (String)parameters.get(Constants.DATABASE_ATTR_STORED_PROCEDURE_NAME);

        // Copy parameters and names for stored procedures and functions
        Vector<String> procParameters = new Vector<String>(INITIAL_PARAMETER_SIZE);

        // Do a qualified guess on the number of parameters passed to the stored
        // procedure; In Query.java we will find the exact number of parameters
        int numberOfParameters = 0;
        for (int i = Constants.DATABASE_STTN_MAX_PARAMS - 1; i >= 0; i--) {
            if ((parameters.get(Constants.DATABASE_ATTR_PROC_PARAM + i) != null) ||
                (parameters.get(Constants.DATABASE_ATTR_RESULTNAME + i) != null)) {
                numberOfParameters = i;
                break;
            }
        }
        log.debug(numberOfParameters + " parameters found");

        // Retrieve the parameter values
        for (int i = 1; i <= numberOfParameters; i++) {
            String procParameterValue = (String)parameters.get(Constants.DATABASE_ATTR_PROC_PARAM + i);
            log.debug("procParameterValue(" + i + "): " + procParameterValue);
            procParameters.add(procParameterValue);
        }
        String viewOutput = null;

        // -----------------------------------------------
        // Security
        // -----------------------------------------------
        if (!SecurityManager.isAuthorized(parameters, accountId, sessionId,
                                          CoreConstants.DATABASE_CMND_USERSTOREDPROCEDURE)) {
            viewOutput =
                    sendErrorToView(VIEW_PACKAGE + viewName, "Authorized login required for accessing the " + CoreConstants.DATABASE_CMND_USERSTOREDPROCEDURE +
                                    " function", log);
        } else {
            AuthResponse authResponse = SecurityManager.touch(sessionId, accountId);

            if (!authResponse.isSuccess()) {
                viewOutput =
                        sendErrorToView(VIEW_PACKAGE + viewName, "Error in accessing the " + CoreConstants.DATABASE_CMND_USERSTOREDPROCEDURE +
                                        " function: " + authResponse.getMessage(), log);
            } else {

                // -----------------------------------------------
                // Call the model
                // -----------------------------------------------
                long timerStartModel = System.currentTimeMillis();
                Hashtable queryResult = null;

                try {
                    queryResult =
                            Query.doUserStoredProcedure(accountId, edu.stanford.ehs.jml.security.model.SecurityManager.getUserId(sessionId),
                                                        storedProcedureName, procParameters);

                } catch (Exception e) {
                    log.error("Error in getting a db connection with the id " + accountId + ": " + e.toString());
                }

                long timerEndModel = System.currentTimeMillis();

                // -----------------------------------------------
                // Call the view
                // -----------------------------------------------
                long timerStartView = System.currentTimeMillis();

                if (queryResult.size() > 0) {
                    viewOutput =
                            callViewMethod(VIEW_PACKAGE + viewName, "doUserStoredProcedure", new Object[] { (Object)queryResult },
                                           log);
                    queryResult = null; // House cleaning
                } else {
                    log.debug("The result set was null and the view was not called.");
                }

                long timerEndView = System.currentTimeMillis();

                log.info("Model time: " + (timerEndModel - timerStartModel) + " ms");
                log.info("View times: " + (timerEndView - timerStartView) + " ms");
                log.info("Total: " + ((timerEndModel - timerStartModel + timerEndView) - timerStartView) + " ms");
            }
        }
        return (viewOutput);
    }

}
