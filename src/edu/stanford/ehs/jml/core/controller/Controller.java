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

import java.io.IOException;

import java.util.Hashtable;
import java.util.concurrent.Callable;

import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Implementation of a Controller Callable used for sumitting the the client request as a thread
 * to the thread pool. This class is primarily usd by the ControllerFactory
 *
 * @see ControllerFactory
 */
public class Controller implements Callable {

    Hashtable parameters = null;
    HttpServletResponse httpResponse = null;
    String stringResponse = null;

    protected static Logger log = LogManager.getLogger(Controller.class.getName());

    /**
     * When the thread is ready for execution, the concurrency control will call this method which will
     * initiate a call to the static process method of ControllerThread.
     *
     * @see ControllerThread#process(Hashtable)
     *
     * @return Always 0
     * @throws java.io.IOException
     */
    public Integer call() throws java.io.IOException {
        try {
            stringResponse = ControllerThread.process(parameters);
        } catch (Exception e) {
            throw new IOException("Error in processing call to ControllerThread: " + e.toString());
        }
        return (0);
    }

    /**
     * Set the parameters to the controller thread. Call this method before submitting the task to the thread pool.
     *
     ** @param parameters
     */
    public void setRequest(Hashtable parameters) {
        this.parameters = parameters;
    }

    /**
     * Set the HttpResponse for returned objects. Call this method before submitting the task to the thread pool.
     *
     * @param httpResponse
     */
    public void setStreamingResponse(HttpServletResponse httpResponse) {
        this.httpResponse = httpResponse;
    }

    /**
     * Get the response from the controller thread. Call this method after the threaded task has finished executing.
     *
     * @return The String response from the controller
     */
    public String getStringResponse() {
        return (stringResponse);
    }

}
