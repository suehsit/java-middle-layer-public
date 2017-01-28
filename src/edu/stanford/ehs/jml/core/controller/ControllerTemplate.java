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

import edu.stanford.ehs.jml.core.model.CoreConstants;

import java.lang.reflect.Method;

import org.apache.logging.log4j.Logger;

/**
 * Controller template containing basic and generic controller methods, mainly used by Controller
 *
 * @see Controller
 */
public class ControllerTemplate {

    /**
     * Call a view, specified by its class and method, pass the raw data arguments and return the view output
     * as a String
     *
     * @param viewClassName The class name of the view to be called
     * @param method The name of the method to be called, e.g. doUserStoredProcedure
     * @param arguments The raw data
     * @param log The logger object
     * @return String object with the formatted output
     */
    protected static String callViewMethod(String viewClassName, String method, Object[] arguments, Logger log) {
        String viewOutput = null;

        try {
            Class viewClass = getViewClass(viewClassName, log);
            Class[] argumentsClasses = null;

            if (arguments != null) {
                int numberOfArguments = arguments.length;

                argumentsClasses = new Class[numberOfArguments];

                for (int i = 0; i < numberOfArguments; i++) {
                    argumentsClasses[i] = arguments[i].getClass();
                    log.debug("Argument " + i + " = " + argumentsClasses[i].getClass().toString());
                }
            }

            Method viewMethod = viewClass.getMethod(method, argumentsClasses);

            viewOutput = (String)viewMethod.invoke((Object)viewClass, arguments);
        } catch (Exception e) {
            log.error("Error in invoking the method " + method + " from " + viewClassName + ": " + e.toString() +
                      " ** Cause: " + e.getCause());
        }

        return (viewOutput);
    }

    /**
     * If an error occurs in the model classes, use this method to pass the error message to the view
     * class.
     *
     * @param viewClass The class <b>name</b> of the view to be called
     * @param errorMessage The clear-text representation of the error message to be passed
     * @param log The logger object
     * @return String object with the formatted error output
     * @see #sendErrorToView(String, String, Logger)
     */
    protected static String sendErrorToView(Class viewClass, String errorMessage, Logger log) {
        String viewOutput = null;

        try {
            Method viewMethod =
                viewClass.getMethod(CoreConstants.GENERAL_CMND_ERRORMESSAGE, new Class[] { errorMessage.getClass() });

            viewOutput = (String)viewMethod.invoke((Object)viewClass, new Object[] { errorMessage });

            log.info(errorMessage);
        } catch (Exception e) {
            log.error("Error in invoking the method errorMessage from " + viewClass.getName() + ": " + e.toString());
        }

        return (viewOutput);
    }

    /**
     * If an error occurs in the model classes, use this method to pass the error message to the view
     * class.
     *
     * @param viewClassName The class <b>object</b> of the view to be called
     * @param errorMessage The clear-text representation of the error message to be passed
     * @param log The logger object
     * @return Rendered view of the error message
     * @see #sendErrorToView(Class, String, Logger)
     */
    protected static String sendErrorToView(String viewClassName, String errorMessage, Logger log) {
        return (sendErrorToView(getViewClass(viewClassName, log), errorMessage, log));
    }

    /**
     * Return the view class object
     * @param viewName The String name of the view class
     * @param log The Logger
     * @return The view class object
     */
    protected static Class getViewClass(String viewName, Logger log) {
        Class viewClass = null;

        try {
            viewClass = Class.forName(viewName);
        } catch (Exception e) {
            log.error("Cannot initialize the view class " + viewName + ": " + e.toString());
        }

        return (viewClass);
    }
}
