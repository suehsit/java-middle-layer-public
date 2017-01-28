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

package edu.stanford.ehs.jml.util;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import sun.misc.BASE64Encoder;


public class CSRFToken {
    private String token;
    
    /**
     * Generate a secure random string to be our CSRF token
     * 
     * "Borrowed" from https://www.owasp.org/index.php/How_CSRFGuard_Works
     */
     public CSRFToken(int size) {
        SecureRandom sr = null;
        byte[] random = new byte[size];
        BASE64Encoder encoder = new BASE64Encoder();
        
        // Determine which algorithm to use for generating the token
        // Most examples I've seen for generating a CSRF token use SHA1PRNG, 
        // so trying to use that one if it's available
        try {
            sr = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            // Use the default implementation of SecureRandom if SHA1PRNG isn't available
            sr = new SecureRandom();
        }
        
        sr.nextBytes(random);
        token = encoder.encode(random);
    }
    
    /**
     * Return a sting version of the CSRF token
     * 
     * @return token
     */
    public String toString() {
        return token;
    }
}
