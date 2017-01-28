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

package edu.stanford.ehs.jml.security.model;

import edu.stanford.ehs.jml.messaging.email.model.SMTPSettings;
import edu.stanford.ehs.jml.database.model.oracle.Query;
import oracle.jdbc.pool.OracleConnectionPoolDataSource;

/**
 * Account class to hold an account's datasource (connection pool), SMTP configuration and security settings.
 */
public class Account {

    private OracleConnectionPoolDataSource connectionPoolDataSource;
    private SMTPSettings smtpSettings;
    private String accountId;
    private String loginClassName;
    private long timeOut;
    private boolean useCSRFToken;
    private boolean logDBMSOutput = false;

    /**
     * Set the connection pool data source (Oracle)
     *
     * @param connectionPoolDataSource
     */
    public void setConnectionPoolDataSource(OracleConnectionPoolDataSource connectionPoolDataSource) {
        this.connectionPoolDataSource = connectionPoolDataSource;
    }

    /**
     * Get the connection pool data source (Oracle)
     *
     * @return OracleConnectionPoolDataSource
     */
    public OracleConnectionPoolDataSource getConnectionPoolDataSource() {
        return (connectionPoolDataSource);
    }

    /**
     * Set the SMTP settings for the account.
     *
     * @param smtpSettings SMTP settings
     */
    public void setSMTPSettings(SMTPSettings smtpSettings) {
        this.smtpSettings = smtpSettings;
    }

    /**
     * Get the SMTP settings for the account.
     *
     * @return SMTP settings
     */
    public SMTPSettings getSMTPSettings() {
        return (smtpSettings);
    }

    /**
     * Get the name/id of the account.
     *
     * @return Account Id/name
     */
    public String getAccountId() {
        return accountId;
    }
    
    /**
     * Get the value of whether or not this account has DBMS output logging enabled
     *
     * @return True or false
     */
    public boolean getLogDBMSOutput() {
        return logDBMSOutput;
    }

    /**
     * Get the login class for the account.
     *
     * @return Name of login class
     */
    public String getLoginClassName() {
        return loginClassName;
    }

    /**
     * Get the session time-out.
     *
     * @return Session time-out in milliseconds
     */
    public long getTimeOut() {
        return timeOut;
    }
    
    /**
     * Get the value of whether or not this account should require a CSRF token for requests.
     *
     * @return True or false
     */
    public boolean getUseCSRFToken() {
        return useCSRFToken;
    }

    /**
     * Set the account name/id.
     *
     * @param accountId Account id or name
     */
    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
    
    /**
     * Set the login class name.
     *
     * @param enable Boolean value of whether or not this account should log DBMS output
     */
    public void setLogDBMSOutput(Boolean enable) {
        logDBMSOutput = enable;
    }

    /**
     * Set the login class name.
     *
     * @param loginClassName Login class name
     */
    public void setLoginClassName(String loginClassName) {
        this.loginClassName = loginClassName;
    }

    /**
     * Set the session time-out in milliseconds.
     *
     * @param timeOut Session time-out (ms)
     */
    public void setTimeOut(long timeOut) {
        this.timeOut = timeOut;
    }
    
    /**
     * Set the value of whether or not this account should require a CSRF token for requests.
     *
     * @param useCSRFToken True or false
     */
    public void setUseCSRFToken(boolean useCSRFToken) {
        this.useCSRFToken = useCSRFToken;
    }
}
