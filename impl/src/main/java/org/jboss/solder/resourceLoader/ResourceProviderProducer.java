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
import java.util.Properties;

import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;

import org.jboss.solder.resourceLoader.ResourceProvider;

/**
 * FIXME This producer serves as a temporary workaround for the GlassFish BDA visiblity problem.
 * Once resolved, remove this class and reenable the @Inject on the ResourceProvider.
 *
 * @author Dan Allen
 */
public class ResourceProviderProducer {
    @Produces
    public ResourceProvider getResourceProvider(@Any Instance<InputStream> inputStreamProvider, @Any Instance<URL> urlProvider, @Any Instance<Collection<InputStream>> inputStreamsProvider, @Any Instance<Collection<URL>> urlsProvider, @Any Instance<Properties> propertiesBundleProvider, @Any Instance<Collection<Properties>> propertiesBundlesProvider) {
        return new ResourceProvider(inputStreamProvider, urlProvider, inputStreamsProvider, urlsProvider, propertiesBundleProvider, propertiesBundlesProvider);
    }
}
