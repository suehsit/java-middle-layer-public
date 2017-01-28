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

package edu.stanford.ehs.jml.database.model.oracle;

import edu.stanford.ehs.jml.security.model.SecurityManager;

import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.sql.SQLException;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;
import java.util.Date;

import oracle.jdbc.OracleCallableStatement;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleDatabaseMetaData;
import oracle.jdbc.OracleTypes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.rowset.CachedRowSetImpl;


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
        boolean logDBMSOutput = SecurityManager.getAccount(client).getLogDBMSOutput();

        Vector<String> columnNames = new Vector<String>(SP_MIN_COLUMN_NAME);
        Vector<String> outputSimpleValue = new Vector<String>(SP_MIN_OUTPUT_SIMPLE_VALUE);
        Vector<String> outputCursor = new Vector<String>(SP_MIN_OUTPUT_CURSOR);

        Hashtable<String, Vector> queryResult = new Hashtable<String, Vector>(SP_MIN_QUERY_RESULT_SIMPLE);

        try {

            // Get the Oracle connection
            long timerStartGetConnection = System.currentTimeMillis();
            connection = ConnectionManager.getOracleConnection(log, client);
            long timerEndGetConnection = System.currentTimeMillis();
            log.info("Time getting connection: " + (timerEndGetConnection - timerStartGetConnection) + " ms");

            // Get the meta information about the stored procedure's columns in
            // order to get the output columns as well as registering the input
            // and output data
            long timerStartGetMetaData= System.currentTimeMillis();
            databaseMetaData = (OracleDatabaseMetaData)connection.getMetaData();
            long timerEndGetMetaData= System.currentTimeMillis();
            log.info("Time getting metadata: " + (timerEndGetMetaData - timerStartGetMetaData) + " ms");

            // Derive the catalog and name pattern from the name of the
            // stored procedure
            String procedureCatalog = null;
            String procedureNamePattern = storedProcedureName;
            String procedureSchema = null;
            int procedureProcedureSearchIndex = procedureNamePattern.lastIndexOf('.');

            if (procedureProcedureSearchIndex != -1) {

                int procedureSchemaSearchIndex = procedureNamePattern.indexOf('.');
                //log.info(procedureNamePattern);

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
           
            /* 
             * Translation of params used for `getColumnMetaData`:
             * client = account name
             * procedureSchema = schema name in the database (could be different from the account name)
             * procedureCatalog = package name
             * procedureNamePattern = procedure name
             */
            // Get the parameters of the stored procedure
            long timerStartGetColumnMetaData= System.currentTimeMillis();
            ResultSet metaDataResultSet = getColumnMetaData(client, procedureSchema, procedureCatalog, procedureNamePattern);
            long timerEndGetColumnMetaData= System.currentTimeMillis();
            log.debug("Time getting column metadata: " + (timerEndGetColumnMetaData - timerStartGetColumnMetaData) + " ms");

            // Find the number of parameters for the stored procedure
            log.debug("Calculating the exact number of parameters for the stored procedure");
            int numberOfParameters = 0;
            while (metaDataResultSet.next())
                numberOfParameters++;

            // Move cursor before first record
            metaDataResultSet.beforeFirst();

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

            log.info("procedureSchema=" + procedureSchema);
            log.info("procedureCatalog=" + procedureCatalog);
            log.info("procedureNamePattern=" + procedureNamePattern);
            log.info("Found " + numberOfParameters + " parameters.");
            log.debug("Time-out: " + callableStatement.getQueryTimeout() + " seconds");
            log.info("SQL string: " + sqlString.toString());

            boolean moreRecords = metaDataResultSet.next();
            int columnCounter = 1;
            while (moreRecords) {

                // Get the column information
                thisColumnType = metaDataResultSet.getInt("COLUMN_TYPE");
                thisDataType = metaDataResultSet.getInt("DATA_TYPE");
                thisColumnName = metaDataResultSet.getString("COLUMN_NAME");
                log.info(thisColumnName + " = (" + thisColumnType + ", " + thisDataType + ")");
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
                
                // Get a timestamp from before we start looping through the resultset
                Date startDate = new Date();
                
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
                
                // We're done with the resultset, now get a timestamp from right now, then calculate how long it took
                Date endDate = new Date();
                log.debug("Time spent looping through resultset: " + (endDate.getTime() - startDate.getTime()) + " ms");
                
                queryResult.put(outputCursorElement_Key, resultSetVector);
            }
            
            // --------------------------------------------------
            // Step 4: Retrieve DBMS output (if it has been enabled for this account)
            // --------------------------------------------------
            if (logDBMSOutput) {
                log.debug("Step 4: Retrieve DBMS output");
                getDBMSOutput(connection);
            };

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
    
    /**
     * Retrieve DBMS output from Oracle and log it for debugging purposes
     *
     * @param connection The connection to use
     * @throws Exception
     */
    public static void getDBMSOutput(OracleConnection connection) throws Exception {
        OracleCallableStatement callableStatement = null;
        int status = 0; // The status of the dbms_output.get_line request
        StringBuffer output = new StringBuffer();

        try {
            // Prepare the statement
            callableStatement = (OracleCallableStatement)connection.prepareCall("begin dbms_output.get_line(?,?); end;");
            
            callableStatement.registerOutParameter(1,java.sql.Types.VARCHAR);
            callableStatement.registerOutParameter(2,java.sql.Types.NUMERIC);

            // Set query timeout to two minutes which will prevent the server to freeze up
            // due to badly formatted queries
            callableStatement.setQueryTimeout(QUERY_TIME_OUT);
            
            log.debug("Time-out: " + callableStatement.getQueryTimeout() + " seconds");
            log.debug("SQL string: sys.dbms_output.get_line");
            
            do {
                callableStatement.execute();
                output.append(callableStatement.getString(1));
                status = callableStatement.getInt(2);
            } while (status == 0);
            
            if (!output.toString().equals("null")) {
                // If debug is not enabled only show the DBMS output in the log if there is something returned by Oracle
                // as info level logging
                log.info(" --> Begin DBMS output");
                log.info(output);
                log.info(" --> End DBMS output");
            } else {
                // If we ARE doing debug logging, then log regardless of whether we get something from Oracle or not
                log.debug(" --> Begin DBMS output");
                log.debug(output);
                log.debug(" --> End DBMS output");
            }
            
        } catch (Exception e) {
            log.debug("Problem occurred during dump of dbms_output " + e.toString());
        }

        // House cleaning
        try {
            callableStatement.close();
        } catch (Exception e) {
            log.warn("Was not able to close callableStatement. " + e.toString());
        }
    }
    
    /**
     * Query column metadata
     *
     * @param account_name The account name
     * @param schema Schema name in the database (could be different from the account name)
     * @param package_name Package name
     * @param proc_name Procedure name
     */
    protected static ResultSet getColumnMetaData(String account_name, String schema, String package_name, String proc_name) {
        ResultSet resultSet = null;
        OracleConnection connection = null;
        CachedRowSetImpl rowset = null;

        PreparedStatement ps = null;
        
        String query = "SELECT argument_name AS column_name," + 
        "       DECODE(position, 0, 5," + 
        "                        DECODE(in_out, 'IN', 1," + 
        "                                       'OUT', 4," + 
        "                                       'IN/OUT', 2," + 
        "                                       0)) AS column_type," + 
        "       DECODE (data_type, 'CHAR', 1," + 
        "                          'VARCHAR2', 12," + 
        "                          'NUMBER', 3," + 
        "                          'LONG', -1," + 
        "                          'DATE', 91," + 
        "                          'RAW', -3," + 
        "                          'LONG RAW', -4," + 
        "                          'TIMESTAMP', 93, " + 
        "                          'TIMESTAMP WITH TIME ZONE', -101, " + 
        "               'TIMESTAMP WITH LOCAL TIME ZONE', -102, " + 
        "               'INTERVAL YEAR TO MONTH', -103, " + 
        "               'INTERVAL DAY TO SECOND', -104, " + 
        "               'BINARY_FLOAT', 100, 'BINARY_DOUBLE', 101, 1111) AS data_type," + 
        "      sequence" + 
        " FROM all_arguments" + 
        " WHERE owner = ? " +
        "  AND package_name = ? " +
        "  AND object_name = ? " +
        " ORDER BY sequence";

        try {
            log.debug("Quering column metadata for " + account_name + ": Getting connection");
            connection = ConnectionManager.getOracleConnection(log, account_name);
            
            ps = connection.prepareStatement(query, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ps.setString(1, schema);
            ps.setString(2, package_name);
            ps.setString(3, proc_name);
            log.debug("Quering column metadata for " + schema + ": Execute query");
            
            resultSet = ps.executeQuery();
            log.debug("Quering column metadata for " + schema + ": Query completed");

        } catch (Exception e) {
            log.error(e.toString());
        }
        
        // ----------------------------------------
        // House cleaning
        // ----------------------------------------
        try {
            rowset = new CachedRowSetImpl();
            rowset.populate(resultSet);
        } catch (SQLException e) {
            log.warn("Was not able to cache resultset. " + e.toString());
        }
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
        
        return rowset;
    }
}
