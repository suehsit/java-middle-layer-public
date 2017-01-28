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

package edu.stanford.ehs.jml.core.model;

import javax.servlet.http.HttpServlet;


/**
 * Initializes the Log4J Logger environment.

 *
 * Example of servlet specification in the web descriptor (web.xml):
 *
 * <pre>
 * <servlet>
 *   <servlet-name>log4j-init</servlet-name>
 *     <servlet-class>edu.stanford.ehs.jml.core.model.Log4jInit</servlet-class>
 *     <init-param>
 *       <param-name>log4j-init-file</param-name>
 *       <param-value>WEB-INF/conf/log4j.lcf</param-value>
 *     </init-param>
 *     <load-on-startup>1</load-on-startup>
 *   </servlet>
 * </pre>
 */
public class Log4jInit extends HttpServlet {

    @SuppressWarnings("compatibility:517856491489596981")
    private static final long serialVersionUID = 1L;

    /**
     * Initialize the Log4J environment with the settings specified in the servlet's
     * parameter 'log4j-init-file".
     */
    public void init() {
        String prefix = getServletContext().getRealPath("/");
        String file = getInitParameter("log4j-init-file");

        if (file != null) {
            System.setProperty("log4j.configurationFile", prefix + file);
            //            PropertyConfigurator.configure(prefix + file);
        }
    }
}
