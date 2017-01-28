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

import edu.stanford.ehs.jml.util.CSRFToken;

/**
 * AuthResponse class to hold authentication messages from security plug-ins.
 */
public class AuthResponse {

    private String message;
    private boolean success;
    private CSRFToken token;

    public AuthResponse() {
    }

    /**
     * Construct an authentication response by specifying the success and a message.
     *
     * @param success Success: true or false
     * @param message Message to follow the response, in particular with negative authentications.
     */
    public AuthResponse(boolean success, String message, CSRFToken token) {
        setSuccess(success);
        setMessage(message);
        setCSRFToken(token);
    }

    /**
     * Get the authentication message.
     *
     * @return Authentication message
     */
    public String getMessage() {
        return message;
    }
    
    /**
     * Get the CSRF token
     * 
     * @return
     */
    public CSRFToken getCSRFToken() {
        return token;
    }
    
    /**
     * Set the CSRF token
     * 
     * @return
     */
    public void setCSRFToken(CSRFToken token) {
        this.token = token;
    }

    /**
     * Get the success of the authentication.
     *
     * @return success Success: true or false
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Set the authentication message.
     *
     * @param message Message to follow the response, in particular with negative authentications.
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Set the success of the authentication.
     *
     * @param success Success: true or false
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }
}
