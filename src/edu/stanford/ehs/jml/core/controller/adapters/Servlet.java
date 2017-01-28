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
import edu.stanford.ehs.jml.core.controller.ControllerThread;
import edu.stanford.ehs.jml.core.model.CoreConstants;

import edu.stanford.ehs.jml.util.XMLUtil;

import java.io.IOException;
import java.io.PrintWriter;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.concurrent.Callable;

import java.util.concurrent.Future;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * HTTP/HTTPS servlet adapter allowing web clients to communicate with the JML using URLs
 * submitted via HTTP or HTTPS.
 */
public class Servlet extends HttpServlet {

    // Adapter Controller constants
    public static final String DEFAULT_VIEW = "JSON";
    protected static Logger log = LogManager.getLogger(Servlet.class.getName());
    @SuppressWarnings("compatibility:2815667822596901198")
    private static final long serialVersionUID = 1L;

    /**
     * Process GET request.
     *
     * @param httpRequest
     * @param httpResponse
     * @throws ServletException
     * @throws IOException
     */
    public void doGet(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException,
                                                                                               IOException {

        Hashtable<String, Object> controllerParameters =
            new Hashtable<String, Object>(20); // String and HTTPServletRequest

        // Put the parameters in the controllerParameters hashtable. This might seem
        // redundant but is needed to align the parameter transfer from both the
        // Servlet and JSP adapters to the main controller
        Enumeration httpRequestParameters = httpRequest.getParameterNames();
        String parameterKey = null;

        while (httpRequestParameters.hasMoreElements()) {
            parameterKey = (String)httpRequestParameters.nextElement();
            controllerParameters.put(parameterKey, XMLUtil.decode(httpRequest.getParameter(parameterKey)));
        }

        // Insert the default view if the request does not contain a specified view
        String viewName = (String)controllerParameters.get(CoreConstants.GENERAL_ATTR_VIEW);

        if (viewName == null) {
            viewName = DEFAULT_VIEW;
            controllerParameters.remove(CoreConstants.GENERAL_ATTR_VIEW);
            controllerParameters.put(CoreConstants.GENERAL_ATTR_VIEW, viewName);
        }

        // Finally, put the HttpServletRequest into the controller parameters
        controllerParameters.put(CoreConstants.GENERAL_ATTR_HTTPREQUEST, httpRequest);

        try {
            Controller controllerThread = new Controller();

            controllerThread.setRequest(controllerParameters);
            controllerThread.setStreamingResponse(httpResponse);

            Future futureResult = ControllerFactory.submit((Callable<Controller>)controllerThread);

            String viewContentType = ControllerThread.getContentType(viewName);
            httpResponse.setContentType(viewContentType);

            PrintWriter outPrintWriter = httpResponse.getWriter();

            // Synchronize the result of the call
            while (!(futureResult.isDone() || futureResult.isCancelled())) {
            }

            outPrintWriter.print(controllerThread.getStringResponse());

            outPrintWriter.close();
        } catch (Exception e) {
            log.debug("An error occured: " + e.toString());
        }
    }

    /**
     *
     * Process POST request by redirecting to the doGet method.
     *
     * @param httpRequest
     * @param httpResponse
     * @throws ServletException
     * @throws IOException
     * @see #doGet(HttpServletRequest, HttpServletResponse)
     */
    public void doPost(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws ServletException,
                                                                                                IOException {
        doGet(httpRequest, httpResponse);
    }

    /**
     * Initialize the HTTP/HTTP servlet adapter
     *
     * @param config
     * @throws ServletException
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }
}
