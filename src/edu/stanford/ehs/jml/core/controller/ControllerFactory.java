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

package edu.stanford.ehs.jml.core.controller;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * The ControllerFactory is the interface for the adapter to the multi-threaded JML controller environment.
 */
public class ControllerFactory {

    protected static Logger log = LogManager.getLogger(ControllerFactory.class.getName());
    
    private static boolean isInitialized = false;

    // The thread pool
    private static ExecutorService executorService = null;

    /**
     * Initialize the cached thread pool which reuses thread that have terminated or creates new threads, if needed.
     * This method is called from the main initialization class, Server
     *
     * @see edu.stanford.ehs.jml.core.model.Server
     *
     */
    public static void initialize() {
        log.debug("Initializing cached tread pool");
        executorService = Executors.newCachedThreadPool();
        isInitialized = true;
    }
    
    /**
     * Is the ControllerFactory already initialized?
     *
     * @return True or false
     */
    public static boolean isInitialized() {
        return (isInitialized);
    }

    /**
     * Submit a Controller task to the thread pool.
     * @param controller The CORE controller to be sumitted to the thread pool.
     * @return Future object of the result
     */
    public static Future submit(Callable<Controller> controller) {

        FutureTask futureTask = new FutureTask((Callable<Controller>)controller);
        Future futureResult = null;
        try {
            futureResult = executorService.submit((Runnable)futureTask); // FutureTask to Runnable
        } catch (Exception e) {
            log.error("Error in submitting task: " + e.toString());
        }
        return (futureResult);
    }

    /**
     * Stop all active processes. This method is called from the main initialization class, Server
     *
     * @see edu.stanford.ehs.jml.core.model.Server
     *
     */
    public static void destroy() {
        log.debug("Shutting down cached thread pool");
        executorService.shutdown();
        log.debug("Cached thread pool shut-down completed");
    }


}
