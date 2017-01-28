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

package edu.stanford.ehs.jml.security.view;

import edu.stanford.ehs.jml.core.view.HTMLTemplate;
import edu.stanford.ehs.jml.security.model.AuthResponse;

/**
 * HTML view for the security module
 */
public class HTML extends HTMLTemplate {

    /**
     * HTML rendering for the login model function
     *
     * @return HTML string of the login model function
     *
     * @see edu.stanford.ehs.jml.security.model.SecurityManager#login
     */
    public static String login(AuthResponse authResponse) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        if (authResponse.isSuccess()) {
            outputView.append("<br/>Login was successful");
        } else {
            outputView.append("<br/>Login failed");
            outputView.append("<br/>Reason: " + authResponse.getMessage());
        }

        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * HTML rendering for the logout model function
     *
     * @return HTML string of the logout model function
     *
     * @see edu.stanford.ehs.jml.security.model.SecurityManager#logout
     */
    public static String logout() {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("Logout was successful");
        addFooter(outputView);

        return (outputView.toString());
    }
}
