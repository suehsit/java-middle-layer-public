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

package edu.stanford.ehs.jml.core.controller.adapters;

import edu.stanford.ehs.jml.core.controller.Controller;
import edu.stanford.ehs.jml.core.controller.ControllerFactory;
import edu.stanford.ehs.jml.core.model.CoreConstants;

import java.util.Hashtable;

import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * JML adapter for local JSP applications
 */
public class JspAdapter {
    public static final String DEFAULT_VIEW = "XML";
    protected static Logger log = LogManager.getLogger(JspAdapter.class.getName());

    /**
     * Process the request originating from the JSP client and return the result as a String.
     *
     * @param jspRequest The packaged request object (Hashtable) from the JSP client
     * @return String representation of the result
     */
    public String processRequest(Hashtable jspRequest) {

        // Insert the default view if the request does not contain a specified view
        if ((String)jspRequest.get(CoreConstants.GENERAL_ATTR_VIEW) == null) {
            jspRequest.remove(CoreConstants.GENERAL_ATTR_VIEW);
            jspRequest.put(CoreConstants.GENERAL_ATTR_VIEW, DEFAULT_VIEW);
        }

        // Specify a new thread to execute the request
        Controller controllerThread = new Controller();
        controllerThread.setRequest(jspRequest);

        // Submit the request
        Future futureResult = ControllerFactory.submit(controllerThread);

        // Synchronize the result of the call
        while (!(futureResult.isDone() || futureResult.isCancelled())) {
        }

        // Return the result
        return (controllerThread.getStringResponse());
    }

}
