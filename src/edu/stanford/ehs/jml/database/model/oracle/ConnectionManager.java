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

import edu.stanford.ehs.jml.core.model.CoreConstants;
import edu.stanford.ehs.jml.security.model.SecurityManager;
import edu.stanford.ehs.jml.database.model.Constants;

import javax.sql.PooledConnection;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

import org.apache.logging.log4j.Logger;

/**
 * the ConnectionManager is responsible for providing Oracle connections
 */
public class ConnectionManager {

    /**
     * Return an Oracle connection from the specified account
     *
     * @param log A logger object
     * @param id The account id
     * @return An Oracle connection
     * @throws Exception
     */
    public static OracleConnection getOracleConnection(Logger log, String id) throws Exception {
        OracleConnectionPoolDataSource oracleDataSource = SecurityManager.getAccount(id).getConnectionPoolDataSource();
        oracleDataSource.setLoginTimeout(Constants.DATABASE_LOGIN_TIMEOUT);
        PooledConnection pooledConnection = oracleDataSource.getPooledConnection();
        OracleConnection connection = (OracleConnection)pooledConnection.getConnection();

        if (log.isDebugEnabled()) {
            log.debug("Is the connection null? " + (connection == null));
            log.debug("Is the oracle data source null? " + (oracleDataSource == null));
            log.debug("Got the database " + oracleDataSource.getDatabaseName());
            log.debug("Build date: " + oracleDataSource.BUILD_DATE);
            log.debug("ID: " + CoreConstants.CACHE_PREFIX + id);
            try {
                if (connection != null) {
                    log.debug("Connection properties: " + connection.toString());
                    log.debug("connection.getAutoClose(): " + connection.getAutoClose());
                    log.debug("connection.getAutoCommit(): " + connection.getAutoCommit());
                    log.debug("connection.getCatalog(): " + connection.getCatalog());
                    log.debug("connection.getClass(): " + connection.getClass().getCanonicalName());
                    //log.debug("connection.getConnectionCachingEnabled(): " +
                    //          connection.getConnectionCachingEnabled());
                    log.debug("connection.getConnectionReleasePriority(): " +
                              connection.getConnectionReleasePriority());
                    log.debug("connection.getCreateStatementAsRefCursor(): " +
                              connection.getCreateStatementAsRefCursor());
                    log.debug("connection.getDefaultExecuteBatch(): " + connection.getDefaultExecuteBatch());
                    log.debug("connection.getDefaultRowPrefetch(): " + connection.getDefaultRowPrefetch());
                    log.debug("connection.getEndToEndECIDSequenceNumber(): " +
                              connection.getEndToEndECIDSequenceNumber());
                    if (connection.getEndToEndMetrics() != null) {
                        log.debug("connection.getEndToEndMetrics(): " + connection.getEndToEndMetrics().toString());
                    }
                    log.debug("connection.getExplicitCachingEnabled(): " + connection.getExplicitCachingEnabled());
                    log.debug("connection.getHoldability(): " + connection.getHoldability());
                    log.debug("connection.getImplicitCachingEnabled(): " + connection.getImplicitCachingEnabled());
                    log.debug("connection.getIncludeSynonyms(): " + connection.getIncludeSynonyms());
                    log.debug("connection.getMetaData(): " + connection.getMetaData().toString());
                    log.debug("connection.getSessionTimeZone(): " + connection.getSessionTimeZone());
                    log.debug("connection.getStatementCacheSize(): " + connection.getStatementCacheSize());
                    log.debug("connection.getStructAttrCsId(): " + connection.getStructAttrCsId());
                    log.debug("connection.getProperties(): " + connection.getProperties().toString());
                    log.debug("connection.getRemarksReporting(): " + connection.getRemarksReporting());
                    log.debug("connection.getRestrictGetTables(): " + connection.getRestrictGetTables());
                    log.debug("connection.getTransactionIsolation(): " + connection.getTransactionIsolation());
                    log.debug("connection.isClosed(): " + connection.isClosed());
                    log.debug("connection.isLogicalConnection(): " + connection.isLogicalConnection());
                    log.debug("connection.isProxySession(): " + connection.isProxySession());
                    log.debug("connection.isReadOnly(): " + connection.isReadOnly());
                    if (connection.getWarnings() != null) {
                        log.debug("connection.getWarnings(): " + connection.getWarnings().toString());
                    }
                    if (connection.getUnMatchedConnectionAttributes() != null) {
                        log.debug("connection.getUnMatchedConnectionAttributes(): " +
                                  connection.getUnMatchedConnectionAttributes().toString());
                    }
                }
            } catch (Exception e) {
                log.warn("Error during reading connection property: " + e.toString());
            }
        }

        return (connection);
    }

}
