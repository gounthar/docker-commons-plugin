/*
 * The MIT License
 *
 * Copyright (c) 2015, CloudBees, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.jenkinsci.plugins.docker.commons.credentials;

import hudson.EnvVars;
import hudson.remoting.VirtualChannel;

import java.io.IOException;
import java.io.Serializable;

/**
 * Represents a locally extracted credentials information.
 *
 * <p>
 * Implementations of this class are created by their corresponding {@link KeyMaterialFactory}
 * implementations. Be sure to call {@link #close} when finished.
 *
 * @author Kohsuke Kawaguchi
 */
public abstract class KeyMaterial2 implements Serializable {

    /**
     * Standardize serialization
     */
    private static final long serialVersionUID = 1L;

    /**
     * {@link KeyMaterial} that does nothing.
     */
    public static final KeyMaterial2 NULL = new NullKeyMaterial();

    /**
     * The environment variables
     */
    private final EnvVars envVars;

    protected KeyMaterial2(EnvVars envVars) {
        this.envVars = envVars;
    }

    /**
     * Get the environment variables needed to be passed when docker runs, to access
     * {@link DockerServerCredentials} that this object was created from.
     */
    public final EnvVars env() {
        return envVars;
    }

    /**
     * Deletes the key materials from the file system. As key materials are copied into files
     * every time {@link KeyMaterialFactory} is created, it must be also cleaned up each time.
     * @param channel to restore pathnames
     */
    public abstract void close(VirtualChannel channel) throws IOException, InterruptedException;

    private static final class NullKeyMaterial extends KeyMaterial2 {
        private static final long serialVersionUID = 1L;
        protected NullKeyMaterial() {
            super(new EnvVars());
        }
        @Override
        public void close(VirtualChannel channel) {
        }
        private Object readResolve() {
            return NULL;
        }
    }
}
