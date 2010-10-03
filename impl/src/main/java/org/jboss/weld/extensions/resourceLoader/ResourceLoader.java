/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.extensions.resourceLoader;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import org.jboss.weld.extensions.util.Sortable;

/**
 * <p>
 * A {@link ResourceLoader} is a pluggable contract for loading resources.
 * </p>
 * 
 * <p>
 * Weld Extensions comes with a resource loader which uses the classpath to
 * locate resources, and a resource loader that uses the
 * {@link javax.servlet.ServletContext} (if available).
 * </p>
 * 
 * <p>
 * To register a custom {@link ResourceLoader} you should implement this
 * interface, and register it as a <a href=
 * "http://download.oracle.com/javase/1.3/docs/guide/jar/jar.html#Service%20Provider"
 * >ServiceProvider</a> for the {@link ResourceLoader} service.
 * </p>
 * 
 * @author Pete Muir
 * 
 * @see ResourceProvider
 * @see Resource
 * 
 */
public interface ResourceLoader extends Sortable
{

   /**
    * Get the {@link URL} for a resource.
    * 
    * @param resource the resource to get the {@link URL} for
    * @return the {@link URL}, or null if the resource does not exist
    */
   public URL getResource(String name);

   /**
    * Get the {@link InputStream} for a resource.
    * 
    * @param name the resource to get the {@link InputStream} for
    * @return the {@link InputStream}, or null if the resource does not exist
    */
   public InputStream getResourceAsStream(String name);

   /**
   public Collection<URL> getResources(String name);

    * 
    * @param name
    * @return
    */
   public Collection<InputStream> getResourcesAsStream(String name);

}
