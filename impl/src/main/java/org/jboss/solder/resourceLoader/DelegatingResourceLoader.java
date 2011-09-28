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
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jboss.solder.logging.Logger;
import org.jboss.solder.resourceLoader.ResourceLoader;

/**
 * Resource loader that delegates to a static list of resource loaders.
 *
 * @author Stuart Douglas
 * @deprecated this resource loader can easily leak between application instances
 */
@Deprecated
public class DelegatingResourceLoader implements ResourceLoader {
    private static final Logger log = Logger.getLogger("org.jboss.solder.resources");

    // TODO: get rid of the static
    private static final List<ResourceLoader> resourceLoaders = new ArrayList<ResourceLoader>();

    public static void addResourceLoader(ResourceLoader loader) {
        resourceLoaders.add(loader);
    }

    public URL getResource(String name) {
        for (ResourceLoader loader : resourceLoaders) {
            URL resource = loader.getResource(name);
            if (resource != null) {
                log.trace("Loaded resource " + name + " from " + loader.toString());
                return resource;
            }
        }
        return null;
    }

    public InputStream getResourceAsStream(String name) {
        for (ResourceLoader loader : resourceLoaders) {
            InputStream resource = loader.getResourceAsStream(name);
            if (resource != null) {
                log.trace("Loaded resource " + name + " from " + loader.toString());
                return resource;
            }
        }
        return null;
    }

    public Set<URL> getResources(String name) {
        Set<URL> resources = new HashSet<URL>();
        for (ResourceLoader loader : resourceLoaders) {
            resources.addAll(loader.getResources(name));
        }
        return resources;
    }

    public Collection<InputStream> getResourcesAsStream(String name) {
        Set<InputStream> resources = new HashSet<InputStream>();
        for (ResourceLoader loader : resourceLoaders) {
            resources.addAll(loader.getResourcesAsStream(name));
        }
        return resources;
    }

    public int getPrecedence() {
        return 5;
    }

}
