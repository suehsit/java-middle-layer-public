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

package edu.stanford.ehs.jml.database.model.mysql;

import edu.stanford.ehs.jml.security.model.SecurityManager;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDatabaseMetaData;
import oracle.jdbc.OracleTypes;

import org.apache.logging.log4j.Logger;

import edu.stanford.ehs.jml.database.model.Constants;

import org.apache.logging.log4j.LogManager;

/**
 * The Query model class contain all implementations of database operations
 */
public class Query {
    protected static Logger log = LogManager.getLogger(Query.class.getName());

    private static int QUERY_TIME_OUT = 120; // In seconds

    // Hashtable optimization parameters. EMAN = Estimated minimum average number (of)
    private static int MIN_SIZE_ROWS = 30;
    private static int SP_MIN_COLUMN_NAME = 20; // EMAN parameters for the stored procedure
    private static int SP_MIN_OUTPUT_SIMPLE_VALUE = 20; // EMAN simple value in the stored procedure's parameters
    private static int SP_MIN_OUTPUT_CURSOR = 10; // EMAN output cursors in the stored procedure's parameters
    private static int SP_MIN_RESULT_ROW = 10;
    private static int SP_MIN_RESULT_ITEM = 10;
    private static int SP_MIN_QUERY_RESULT_SIMPLE = 50; // EMAN returned rows of simple values, not counting cursors
    private static int SP_MIN_QUERY_RESULT_CURSOR = 50; // EMAN returned rows in any returned cursor

    /**
     * Query all database accounts with the specified query
     *
     * @param query
     */
    protected static void pingAllDatabaseAccounts(String query) {
        Enumeration allAccountNames = SecurityManager.getActiveAccountNames();
        while (allAccountNames.hasMoreElements()) {
            pingDatabaseAccount((String)allAccountNames.nextElement(), query);
        }
    }

    /**
     * Query a specific database account
     *
     * @param client The account ID
     * @param query Plain SQL query
     */
    protected static void pingDatabaseAccount(String client, String query) {
        ResultSet resultSet = null;
        OracleConnection connection = null;

        Statement sqlStatement = null;

        try {
            log.debug("Ping " + client + ": Getting connection");

            connection = ConnectionManager.getOracleConnection(log, client);
            sqlStatement = connection.createStatement();
            log.debug("Ping " + client + ": " + query);
            resultSet = sqlStatement.executeQuery(query);
            log.debug("Ping " + client + ": Query completed");

            while (resultSet.next()) {
                log.debug("Ping " + client + ": Result -> " + resultSet.getString(1));
            }

        } catch (Exception e) {
            log.error(e.toString());
        }


        // ----------------------------------------
        // House cleaning
        // ----------------------------------------
        try {
            connection.close();
            connection = null;
        } catch (Exception e) {
            log.warn("Was not able to close connection. " + e.toString());
        }
        try {
            resultSet.close();
        } catch (Exception e) {
            log.warn("Was not able to close resultset. " + e.toString());
        }
        try {
            // sqlStatement.close();
        } catch (Exception e) {
            log.warn("Was not able to close statement. " + e.toString());
        }
        try {
            // preparedStatement.close();
        } catch (Exception e) {
            log.warn("Was not able to close prepared statement. " + e.toString());
        }
    }

    /**
     * Execute a stored procedure with the specified parameter set including the specified client/account and user name
     * as parameter 1 and 2.
     *
     * @param client The account id
     * @param userName The user id
     * @param storedProcedureName The name of the stored procedure including the catalog name and package name, if needed
     * @param parameters The parameters for the stored procedure.
     * @return The query result
     * @throws Exception
     * @see #doStoredProcedure
     */
    public static Hashtable doUserStoredProcedure(String client, String userName, String storedProcedureName,
                                                  Vector<String> parameters) throws Exception {

        parameters.insertElementAt(userName, 0);
        parameters.insertElementAt(client, 0);
        return (doStoredProcedure(client, storedProcedureName, parameters));
    }

    /**
     * Execute a stored procedure with the specified parameter set
     *
     * @param client The account id
     * @param storedProcedureName The name of the stored procedure including the catalog name and package name, if needed
     * @param parameters The parameters for the stored procedure.
     * @return The query result
     * @throws Exception
     */
    public static Hashtable doStoredProcedure(String client, String storedProcedureName,
                                              Vector parameters) throws Exception {
        OracleConnection connection = null;
        OracleCallableStatement callableStatement = null;
        OracleDatabaseMetaData databaseMetaData = null;
        int thisColumnType;
        int thisDataType;
        String thisColumnName;
        StringBuffer sqlString = new StringBuffer();
        ResultSet cursorResultSet = null;
        ResultSetMetaData thisResultSetMetaData = null;

        Vector<String> columnNames = new Vector<String>(SP_MIN_COLUMN_NAME);
        Vector<String> outputSimpleValue = new Vector<String>(SP_MIN_OUTPUT_SIMPLE_VALUE);
        Vector<String> outputCursor = new Vector<String>(SP_MIN_OUTPUT_CURSOR);

        Hashtable<String, Vector> queryResult = new Hashtable<String, Vector>(SP_MIN_QUERY_RESULT_SIMPLE);

        try {

            // Get the Oracle connection
            connection = ConnectionManager.getOracleConnection(log, client);

            // Get the meta information about the stored procedure's columns in
            // order to get the output columns as well as registering the input
            // and output data
            databaseMetaData = (OracleDatabaseMetaData)connection.getMetaData();

            // Derive the catalog and name pattern from the name of the
            // stored procedure
            String procedureCatalog = null;
            String procedureNamePattern = storedProcedureName;
            String procedureSchema = null;
            int procedureProcedureSearchIndex = procedureNamePattern.lastIndexOf('.');

            if (procedureProcedureSearchIndex != -1) {

                int procedureSchemaSearchIndex = procedureNamePattern.indexOf('.');

                if (procedureSchemaSearchIndex == procedureProcedureSearchIndex) {
                    procedureCatalog = storedProcedureName.substring(0, procedureProcedureSearchIndex);
                    procedureNamePattern = storedProcedureName.substring(procedureProcedureSearchIndex + 1);
                } else {
                    procedureSchema = storedProcedureName.substring(0, procedureSchemaSearchIndex);
                    procedureCatalog =
                            storedProcedureName.substring(procedureSchemaSearchIndex + 1, procedureProcedureSearchIndex);
                    procedureNamePattern = storedProcedureName.substring(procedureProcedureSearchIndex + 1);
                }
            }
            if (procedureSchema == null) {
                procedureSchema = databaseMetaData.getUserName();
            }

            // Get the parameters of the stored procedure
            ResultSet metaDataResultSet =
                databaseMetaData.getProcedureColumns(procedureCatalog, procedureSchema, procedureNamePattern, "%");

            // Find the number of parameters for the stored procedure
            log.debug("Calculating the exact number of parameters for the stored procedure");
            int numberOfParameters = 0;
            while (metaDataResultSet.next())
                numberOfParameters++;
            metaDataResultSet =
                    databaseMetaData.getProcedureColumns(procedureCatalog, procedureSchema, procedureNamePattern,
                                                         "%"); // Reset result set

            // Build the SQL string
            sqlString.append("BEGIN ");
            sqlString.append(storedProcedureName);
            sqlString.append("(");
            for (int i = 0; i < numberOfParameters - 1; i++)
                sqlString.append("?,");
            if (parameters.size() > 0)
                sqlString.append("?");
            sqlString.append("); END;");

            // Prepare the statement
            callableStatement = (OracleCallableStatement)connection.prepareCall(sqlString.toString());

            // Set query timeout to two minutes which will prevent the server to freeze up
            // due to badly formatted queries
            callableStatement.setQueryTimeout(QUERY_TIME_OUT);

            if (log.isDebugEnabled()) {
                log.debug("procedureSchema=" + procedureSchema);
                log.debug("procedureCatalog=" + procedureCatalog);
                log.debug("procedureNamePattern=" + procedureNamePattern);
                log.debug("Found " + numberOfParameters + " parameters.");
                log.debug("Time-out: " + callableStatement.getQueryTimeout() + " seconds");
                log.debug("SQL string: " + sqlString.toString());
            } else {
                log.info("Stored procedure/function: " + storedProcedureName);
            }

            boolean moreRecords = metaDataResultSet.next();
            int columnCounter = 1;
            while (moreRecords) {

                // Get the column information
                thisColumnType = metaDataResultSet.getInt("COLUMN_TYPE");
                thisDataType = metaDataResultSet.getInt("DATA_TYPE");
                thisColumnName = metaDataResultSet.getString("COLUMN_NAME");
                log.debug(thisColumnName + " = (" + thisColumnType + ", " + thisDataType + ")");
                columnNames.add(thisColumnName);

                // Set input parameters with values
                if ((thisColumnType == ParameterMetaData.parameterModeInOut) ||
                    (thisColumnType == ParameterMetaData.parameterModeIn)) {
                    if (parameters.size() >= columnCounter) {
                        if (parameters.get(columnCounter - 1) != null) {
                            log.info("Set input parameter " + columnCounter + " with " +
                                      parameters.get(columnCounter - 1));
                            callableStatement.setString(columnCounter, (String)parameters.get(columnCounter - 1));
                        }
                    }
                }

                // Register output parameters
                if ((thisColumnType == ParameterMetaData.parameterModeInOut) ||
                    (thisColumnType == ParameterMetaData.parameterModeOut)) {

                    // Deal with CURSOR return values
                    if (thisDataType == Types.OTHER) {
                        outputCursor.add(thisColumnName);
                        log.debug("Register out parameter " + columnCounter + " with CURSOR");
                        callableStatement.registerOutParameter(columnCounter, OracleTypes.CURSOR);
                    }
                    // Deal with other return values than CURSOR
                    else {
                        log.debug("Register out parameter " + columnCounter + " with VARCHAR");
                        outputSimpleValue.add(thisColumnName);
                        callableStatement.registerOutParameter(columnCounter, OracleTypes.VARCHAR);
                    }
                }

                moreRecords = metaDataResultSet.next();
                columnCounter++;
            }

            // ----------------------------------------------
            // Step 1: Call the stored procedure
            // ----------------------------------------------
            log.debug("1. Calling stored procedure");
            callableStatement.executeUpdate();
            SQLWarning sqlWarning = callableStatement.getWarnings();
            if (sqlWarning != null) {
                while (sqlWarning != null) {
                    log.warn("Message: " + sqlWarning.getMessage());
                    log.warn("SQLState: " + sqlWarning.getSQLState());
                    log.warn("Vendor error code: " + sqlWarning.getErrorCode());
                    sqlWarning = sqlWarning.getNextWarning();
                }
            }

            // --------------------------------------------------
            // Step 2: Retrieve the simple value return values
            // --------------------------------------------------
            log.debug("Step 2: Retrieve the simple value return values");
            String outputSimpleValueElement_Key = null;
            String outputSimpleValueElement_Value = null;
            Enumeration outputSimpleValueEnumerator = outputSimpleValue.elements();
            while (outputSimpleValueEnumerator.hasMoreElements()) {
                outputSimpleValueElement_Key = (String)outputSimpleValueEnumerator.nextElement();

                // Save the result
                Vector<Hashtable> resultRow = new Vector<Hashtable>(SP_MIN_RESULT_ROW);
                Hashtable<String, String> resultItem = new Hashtable<String, String>(SP_MIN_RESULT_ITEM);
                int indexOfValue = columnNames.indexOf(outputSimpleValueElement_Key) + 1;
                try {
                    outputSimpleValueElement_Value = callableStatement.getString(indexOfValue);
                    resultItem.put(outputSimpleValueElement_Key, outputSimpleValueElement_Value);
                    resultRow.add(resultItem);
                    queryResult.put(outputSimpleValueElement_Key, resultRow);
                } catch (Exception e) {
                    log.error(e.toString() + " : Error in finding the right index for " +
                              outputSimpleValueElement_Key + ". The index returned was " + indexOfValue);
                }
            }

            // --------------------------------------------------
            // Step 3: Retrieve the cursors
            // --------------------------------------------------
            log.debug("Step 3: Retrieve the cursors");
            String outputCursorElement_Key = null;
            int outputCursorElement_KeyIndex;

            // Get all the cursors return values in the stored procedure
            Enumeration outputCursorValueEnumerator = outputCursor.elements();
            log.debug("Number of cursors registered: " + outputCursor.size());
            while (outputCursorValueEnumerator.hasMoreElements()) {

                // Get the logical name of the cursor
                outputCursorElement_Key = (String)outputCursorValueEnumerator.nextElement();
                log.debug("outputCursorElement_Key=" + outputCursorElement_Key);

                // Get the parameter index of the cursor in the stored procedure
                outputCursorElement_KeyIndex = columnNames.indexOf(outputCursorElement_Key) + 1;
                log.debug("outputCursorElement_KeyIndex=" + outputCursorElement_KeyIndex);

                // Retrieve the result set for the cursor
                cursorResultSet = callableStatement.getCursor(outputCursorElement_KeyIndex);

                // Get the names of the columns in the result set of the cursor
                thisResultSetMetaData = cursorResultSet.getMetaData();
                int numberOfColums = thisResultSetMetaData.getColumnCount();
                Vector<String> thisColumnNames = new Vector<String>(numberOfColums);
                for (int i = 1; i <= numberOfColums; i++) {
                    thisResultSetMetaData.getColumnName(i).toLowerCase();
                    thisColumnNames.add(thisResultSetMetaData.getColumnName(i).toLowerCase());
                }

                // Fetch row by row in the cursor's result set
                Vector<Hashtable> resultSetVector = new Vector<Hashtable>(SP_MIN_QUERY_RESULT_CURSOR);
                while (cursorResultSet.next()) {
                    Hashtable<String, String> rowData = new Hashtable<String, String>(SP_MIN_QUERY_RESULT_CURSOR);
                    for (int i = 1; i <= numberOfColums; i++) {
                        try {
                            rowData.put(thisColumnNames.get(i - 1), cursorResultSet.getString(i));
                        } catch (Exception e) {
                            rowData.put(thisColumnNames.get(i - 1), "");
                        }
                    }
                    resultSetVector.add(rowData);
                }
                queryResult.put(outputCursorElement_Key, resultSetVector);
            }

        } catch (Exception e) {
            log.error(e.toString());

            SQLWarning sqlConnectionWarning = connection.getWarnings();
            while (sqlConnectionWarning != null) {
                log.error("SQLState: " + sqlConnectionWarning.getSQLState());
                log.error("Message: " + sqlConnectionWarning.getMessage());
                log.error("Vendor: " + sqlConnectionWarning.getErrorCode());
                sqlConnectionWarning = sqlConnectionWarning.getNextWarning();
            }

            SQLWarning sqlCallableStatementWarning = callableStatement.getWarnings();
            while (sqlCallableStatementWarning != null) {
                log.error("SQLState: " + sqlCallableStatementWarning.getSQLState());
                log.error("Message: " + sqlCallableStatementWarning.getMessage());
                log.error("Vendor: " + sqlCallableStatementWarning.getErrorCode());
                sqlCallableStatementWarning = sqlCallableStatementWarning.getNextWarning();
            }
        }

        // House cleaning
        try {
            connection.close();
            connection = null;
        } catch (Exception e) {
            log.warn("Was not able to close connection. " + e.toString());
        }
        try {
            callableStatement.close();
        } catch (Exception e) {
            log.warn("Was not able to close callableStatement. " + e.toString());
        }
        try {
            cursorResultSet.close();
        } catch (Exception e) {
            log.warn("Was not able to close cursorResultSet. " + e.toString());
        }
        outputSimpleValue = null;
        outputCursor = null;
        columnNames = null;
        sqlString = null;

        return (queryResult);
    }
}
