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

import edu.stanford.ehs.jml.core.view.XMLTemplate;
import edu.stanford.ehs.jml.security.model.AuthResponse;

/**
 * XML view for the security module
 */
public class XML extends XMLTemplate {

    /**
     * XML rendering for the login model function
     *
     * @return XML string of the login model function
     *
     * @see edu.stanford.ehs.jml.security.model.SecurityManager#login
     */
    public static String login(AuthResponse authResponse) {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);

        if (authResponse.isSuccess()) {
            outputView.append("<login result='true' message='Login was successful'/>");
        } else {
            outputView.append("<login result='false' message='Login was not successful'/>");
        }

        addFooter(outputView);

        return (outputView.toString());
    }

    /**
     * XML rendering for the logout model function
     *
     * @return XML string of the logout model function
     *
     * @see edu.stanford.ehs.jml.security.model.SecurityManager#logout
     */
    public static String logout() {
        StringBuffer outputView = new StringBuffer();

        addHeader(outputView);
        outputView.append("<logout result='true' message='Logout was successful'/>");
        addFooter(outputView);

        return (outputView.toString());
    }
}
