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

import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpServlet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Servlet to clean up sessions that are timed out.
 */
public class SecurityManagerGC extends HttpServlet {
    protected static Logger log = LogManager.getLogger(SecurityManagerGC.class.getName());
    @SuppressWarnings("compatibility:-380743349244498859")
    private static final long serialVersionUID = 1L;
    protected transient Timer gcTimer;
    protected long delay;

    /**
     * Initialize the servlet by reading the timer delay in the servlet parameters and then schedule
     * the next clean up.
     */
    public void init() {
        log.debug("Initializing session garbage collector");

        // Retrieve the frequency in minutes
        try {
            Integer timerDelayInt = new Integer(getInitParameter("delay"));

            // Convert delay in minutes to milliseconds
            delay = timerDelayInt.intValue() * 60 * 1000;

        } catch (Exception e) {
            log.error("Could not set the frequency in minutes. The value in web.xml is " + getInitParameter("delay") +
                      ". Setting the default frequency: " + Constants.SECURITY_DEFAULT_FREQUENCY + " minutes");
        }

        scheduleNextRun();

    }

    /**
     * Schedule the next clean up by creating a new TimerTask.
     */
    private void scheduleNextRun() {

        try {
            // Set up the timer
            TimerTask sessionManagerGCTask = new SessionManagerGCTask();
            gcTimer = new Timer();
            gcTimer.schedule(sessionManagerGCTask, delay);
        } catch (Exception e) {
            log.error("Error in initializing the timer: " + e.toString());
        }

    }

    /**
     * Finalize the servlet.
     */
    public void destroy() {
        gcTimer.cancel();
        super.destroy();
    }

    /**
     * Private class containing the timed task that calls the security manager for deleting
     * time out sessions.
     */
    public class SessionManagerGCTask extends TimerTask {
        public void run() {
            log.info("Garbage-collecting expired sessions");
            SecurityManager.deleteTimedOutSessions();

            scheduleNextRun();
        }
    } // end class AccountManagerGCTask

}
