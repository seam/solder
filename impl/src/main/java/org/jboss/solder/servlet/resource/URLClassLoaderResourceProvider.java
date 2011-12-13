/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.solder.servlet.resource;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.jboss.solder.logging.Logger;

/**
 * This implementation of {@link WebResourceLocationProvider} will try to identify the location of web resources by examining
 * the URLs the classloader uses for loading classes. This will only work if the classloader is a {@link URLClassLoader}.
 * 
 * @author Christian Kaltepoth <christian@kaltepoth.de>
 * 
 */
public class URLClassLoaderResourceProvider implements WebResourceLocationProvider {

    private final Logger log = Logger.getLogger(URLClassLoaderResourceProvider.class);

    @Override
    public int getPrecedence() {
        return 100;
    }

    @Override
    public URL getWebResource(String path, ClassLoader classLoader) {

        // this class only works for URLClassLoaders
        if (classLoader instanceof URLClassLoader) {

            URLClassLoader urlClassLoader = (URLClassLoader) classLoader;

            // get the URLs of the classloader
            for (URL classLoaderUrl : urlClassLoader.getURLs()) {

                // try this URL to locate the web resource
                URL possibleWebResourceLocation = processClassLoaderSearchPath(classLoaderUrl, path);

                // we will use the first result we get
                if (possibleWebResourceLocation != null) {
                    return possibleWebResourceLocation;
                }

            }

        }

        // log class loader type in all other cases
        else {
            if (log.isTraceEnabled()) {
                log.trace("Context class loader is not an URLClassLoader but: "
                        + (classLoader != null ? classLoader.getClass().getName() : "null"));
            }
        }

        return null;

    }

    /**
     * Try to locate the web resource using the supplied classloader URL.
     * 
     * @param classPathUrl The {@link URL} the classloader uses to look for classes
     * @param path The path to lookup (e.g. "/WEB-INF/web.xml")
     * @return The guessed location of the web resource or <code>null</code> if it could not be determined
     */
    private URL processClassLoaderSearchPath(URL classPathUrl, String path) {

        // we use string comparisons here
        String location = classPathUrl.toString();

        // ignore JAR files as they don't help us
        if (location.endsWith(".jar")) {
            return null;
        }

        if (log.isTraceEnabled()) {
            log.trace("Found URL of directory: " + location);
        }

        // should always work as the URL is built using an existing URL
        try {

            // Works for most containers like Tomcat, Jetty and Glassfish
            if (location.endsWith("/WEB-INF/classes/")) {
                return new URL(location.substring(0, location.length() - 17) + path);
            }

            // special hack for the Maven Jetty plugin
            if (location.endsWith("/target/classes/")) {
                return new URL(location.substring(0, location.length() - 16) + "/src/main/webapp" + path);
            }

        } catch (MalformedURLException e) {
            if (log.isDebugEnabled()) {
                log.debug("Failed to create URL instance!", e);
            }
        }
        return null;

    }
}
