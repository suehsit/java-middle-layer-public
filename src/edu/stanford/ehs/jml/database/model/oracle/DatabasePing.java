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

import edu.stanford.ehs.jml.database.model.Constants;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;

/**
 * Database pinger servlet that queries the database with regular intervals
 */
public class DatabasePing extends HttpServlet {
    protected static Logger log = LogManager.getLogger(DatabasePing.class.getName());
    @SuppressWarnings("compatibility:549032325993960622")
    private static final long serialVersionUID = 1L;
    protected transient Timer databasePingTimer;

    protected long delay;
    protected String query;

    /**
     * Initialize the servlet
     */
    public void init() {
        if (getInitParameter("active").equals("true")) {

            log.debug("Initializing database pinger");

            // Retrieve the frequency in minutes
            try {
                Integer timerDelayInt = new Integer(getInitParameter("delay"));

                // Convert delay in minutes to milliseconds
                delay = timerDelayInt.intValue() * 60 * 1000;
            } catch (Exception e) {
                log.error("Could not set the frequency in minutes. The value in web.xml is " +
                          getInitParameter("delay") + ". Setting the default frequency: " +
                          Constants.DBPING_DEFAULT_FREQUENCY + " minutes");
                delay = Constants.DBPING_DEFAULT_FREQUENCY;
            }

            // Retrieve the frequency in minutes
            try {
                query = getInitParameter("query");
            } catch (Exception e) {
                log.error("Could not set the query. The value in web.xml is " + getInitParameter("query") +
                          ". Setting the default query: " + Constants.DATABASE_PING_SQL);
                query = Constants.DATABASE_PING_SQL;
            }


            scheduleNextRun();
        } else {
            log.debug("Database pinger will not be activated");
        }

    }

    /**
     * Schedule the next time that the database pinger should run uding the TimerTask class.
     */
    private void scheduleNextRun() {

        try {
            // Set up the timer
            TimerTask databasePingTask = new DatabasePingTask();
            databasePingTimer = new Timer();
            databasePingTimer.schedule(databasePingTask, delay);
        } catch (Exception e) {
            log.error("Error in initializing the timer: " + e.toString());
        }
    }

    /**
     * Servlet destroy method. Include cancelation of the scheduled ping task
     */
    public void destroy() {
        databasePingTimer.cancel();
        super.destroy();
    }

    /**
     * The TimerTask implementing the database ping task
     */
    private class DatabasePingTask extends TimerTask {

        /**
         * Ping the query by utilizing the pingAllDatabaseAccounts method in Query. After the query is completed,
         * schedule the next task.
         */
        public void run() {
            Query.pingAllDatabaseAccounts(query);
            scheduleNextRun();
        }

    }
}
