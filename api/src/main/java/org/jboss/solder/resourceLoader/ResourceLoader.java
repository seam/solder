/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.solder.resourceLoader;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import org.jboss.solder.util.Sortable;

/**
 * <p>
 * A {@link ResourceLoader} is a pluggable contract for loading resources.
 * </p>
 * <p/>
 * <p>
 * Solder comes with a resource loader which uses the classpath to
 * locate resources, and a resource loader that uses the
 * {@link javax.servlet.ServletContext} (if available).
 * </p>
 * <p/>
 * <p>
 * To register a custom {@link ResourceLoader} you should implement this
 * interface, and register it as a <a href=
 * "http://download.oracle.com/javase/1.3/docs/guide/jar/jar.html#Service%20Provider"
 * >ServiceProvider</a> for the {@link ResourceLoader} service.
 * </p>
 *
 * @author Pete Muir
 * @see ResourceProvider
 * @see Resource
 */
public interface ResourceLoader extends Sortable {

    /**
     * <p>
     * Get the {@link URL} for a resource.
     * </p>
     * <p/>
     * <p>
     * The resource loaders are searched in precedence order, stopping when a
     * resource is found.
     * </p>
     *
     * @param name the resource to get the {@link URL} for
     * @return the {@link URL}, or null if the resource does not exist
     * @throws RuntimeException if an error occurs loading the resource
     */
    public URL getResource(String name);

    /**
     * <p>
     * Get the {@link InputStream} for a resource.
     * </p>
     * <p/>
     * <p>
     * The resource loaders are searched in precedence order, stopping when a
     * resource is found.
     * </p>
     *
     * @param name the resource to get the {@link InputStream} for
     * @return the {@link InputStream}, or null if the resource does not exist
     * @throws RuntimeException if an error occurs loading the resource
     */
    public InputStream getResourceAsStream(String name);

    /**
     * <p>
     * Get the URLs known to all resource loaders for a given name.
     * </p>
     *
     * @param name the resource to get the URLs for
     * @return the URLs, or an empty collection if no resources are found
     * @throws RuntimeException if an error occurs loading the resource
     */
    public Collection<URL> getResources(String name);

    /**
     * <p>
     * Get the input streams known to all resource loaders for a given name.
     * </p>
     *
     * @param name name the resource to get the input streams for
     * @return the input streams, or an empty collection if no resources are
     *         found
     * @throws RuntimeException if an error occurs loading the resource
     */
    public Collection<InputStream> getResourcesAsStream(String name);

}
