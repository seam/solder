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

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PreDestroy;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;

import org.jboss.solder.core.Veto;
import org.jboss.solder.reflection.AnnotationInstanceProvider;

/**
 * <p>
 * The ResourceProvider allows dynamic loading of managed resources. For
 * example:
 * </p>
 * <p/>
 * <pre>
 * &#064;Inject
 * void readXml(ResourceProvider provider, String fileName)
 * {
 *    InputStream webXml = provider.loadResourceStream(fileName);
 * }
 * </pre>
 * <p/>
 * <p>
 * If you know the name of the resource you are loading at development time you
 * can inject it directly using the <code>&#64;{@link Resource}</code>
 * qualifier.
 * </p>
 * <p/>
 * <p>
 * If a input stream is loaded, it will be automatically closed when the
 * InputStream goes out of scope. If a URL is used to create an input stream,
 * the application is responsible for closing it. For this reason it is
 * recommended that managed input streams are used where possible.
 * </p>
 *
 * @author Pete Muir
 * @see Resource
 */
@Veto
public class ResourceProvider implements Serializable {

    private static final long serialVersionUID = -4463427096501401965L;

    private final transient AnnotationInstanceProvider annotationInstanceProvider = new AnnotationInstanceProvider();
    ;

    private final Instance<URL> urlProvider;
    private final Instance<InputStream> inputStreamProvider;
    private final Instance<Properties> propertiesBundleProvider;

    private final Instance<Collection<URL>> urlsProvider;
    private final Instance<Collection<InputStream>> inputStreamsProvider;
    private final Instance<Collection<Properties>> propertiesBundlesProvider;

    // Workaround WELD-466
    private final Set<InputStream> streamsCache;

    //@Inject
    ResourceProvider(@Any Instance<InputStream> inputStreamProvider, @Any Instance<URL> urlProvider, @Any Instance<Collection<InputStream>> inputStreamsProvider, @Any Instance<Collection<URL>> urlsProvider, @Any Instance<Properties> propertiesBundleProvider, @Any Instance<Collection<Properties>> propertiesBundlesProvider) {
        this.inputStreamProvider = inputStreamProvider;
        this.urlProvider = urlProvider;
        this.urlsProvider = urlsProvider;
        this.inputStreamsProvider = inputStreamsProvider;
        this.propertiesBundleProvider = propertiesBundleProvider;
        this.propertiesBundlesProvider = propertiesBundlesProvider;
        this.streamsCache = new HashSet<InputStream>();
    }

    /**
     * <p>
     * Load a resource.
     * </p>
     * <p/>
     * <p>
     * The resource loaders will be searched in precedence order, the first
     * result found being returned. The default search order is:
     * </p>
     * <p/>
     * <ul>
     * <li>classpath</li>
     * <li>servlet context, if available</li>
     * </ul>
     * <p/>
     * <p>
     * However extensions may extend this list.
     * </p>
     *
     * @param name the resource to load
     * @return an input stream providing access to the resource, or
     *         <code>null</code> if no resource can be loaded
     * @throws RuntimeException if an error occurs loading the resource
     */
    public InputStream loadResourceStream(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("You must specify the name of the resource to load");
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("value", name);
        InputStream stream = inputStreamProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
        // Workaround WELD-466
        streamsCache.add(stream);
        return stream;
    }

    /**
     * <p>
     * Load all resources known to the resource loader by name.
     * </p>
     * <p/>
     * <p>
     * By default, Solder will search:
     * </p>
     * <p/>
     * <ul>
     * <li>classpath</li>
     * <li>servlet context, if available</li>
     * </ul>
     * <p/>
     * <p>
     * However extensions may extend this list.
     * </p>
     *
     * @param name the resource to load
     * @return a collection of input streams pointing to the resources, or an
     *         empty collection if no resources are found
     * @throws RuntimeException if an error occurs loading the resource
     */
    public Collection<InputStream> loadResourcesStreams(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("You must specify the name of the resource to load");
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("value", name);
        Collection<InputStream> streams = inputStreamsProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
        // Workaround WELD-466
        streamsCache.addAll(streams);
        return streams;
    }

    /**
     * <p>
     * Load a resource.
     * </p>
     * <p/>
     * <p>
     * The resource loaders will be searched in precedence order, the first
     * result found being returned. The default search order is:
     * </p>
     * <p/>
     * <ul>
     * <li>classpath</li>
     * <li>servlet context, if available</li>
     * </ul>
     * <p/>
     * <p>
     * However extensions may extend this list.
     * </p>
     *
     * @param name the resource to load
     * @return a URL pointing to the resource, or <code>null</code> if no
     *         resource can be loaded
     * @throws RuntimeException if an error occurs loading the resource
     */
    public URL loadResource(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("You must specify the name of the resource to load");
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("value", name);
        return urlProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
    }

    /**
     * <p>
     * Load all resources known to the resource loader by name.
     * </p>
     * <p/>
     * <p>
     * By default, Solder will search:
     * </p>
     * <p/>
     * <ul>
     * <li>classpath</li>
     * <li>servlet context, if available</li>
     * </ul>
     * <p/>
     * <p>
     * However extensions may extend this list.
     * </p>
     *
     * @param name the resource to load
     * @return a collection of URLs pointing to the resources, or an empty
     *         collection if no resources are found
     * @throws RuntimeException if an error occurs loading the resource
     */
    public Collection<URL> loadResources(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("You must specify the name of the resource to load");
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("value", name);
        return urlsProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
    }

    /**
     * <p>
     * Load a properties bundle.
     * </p>
     * <p/>
     * <p>
     * The resource loaders will be searched in precedence order, the first
     * result found being returned. The default search order is:
     * </p>
     * <p/>
     * <ul>
     * <li>classpath</li>
     * <li>servlet context, if available</li>
     * </ul>
     * <p/>
     * <p>
     * However extensions may extend this list.
     * </p>
     *
     * @param name the name of the properties bundle to load
     * @return a set of properties, or an empty set if no properties bundle can
     *         be loaded
     * @throws RuntimeException if an error occurs loading the resource
     */
    public Properties loadPropertiesBundle(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("You must specify the name of the properties bundle to load");
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("value", name);
        return propertiesBundleProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
    }

    /**
     * <p>
     * Load all properties bundles known to the resource loader by name.
     * </p>
     * <p/>
     * <p>
     * By default, Solder will search:
     * </p>
     * <p/>
     * <ul>
     * <li>classpath</li>
     * <li>servlet context, if available</li>
     * </ul>
     * <p/>
     * <p>
     * However extensions may extend this list.
     * </p>
     *
     * @param name the name of the properties bundle to load
     * @return a collection of properties bundles, or an empty collection if no
     *         resources are found
     * @throws RuntimeException if an error occurs loading the properties bundle
     */
    public Collection<Properties> loadPropertiesBundles(String name) {
        if (name == null || name.equals("")) {
            throw new IllegalArgumentException("You must specify the name of the properties bundles to load");
        }
        Map<String, Object> values = new HashMap<String, Object>();
        values.put("value", name);
        return propertiesBundlesProvider.select(annotationInstanceProvider.get(Resource.class, values)).get();
    }

    @SuppressWarnings("unused")
    @PreDestroy
    private void cleanup() {
        for (InputStream stream : streamsCache) {
            try {
                stream.close();
            } catch (IOException e) {
                // Nothing we can do about this
            }
        }
    }

}
