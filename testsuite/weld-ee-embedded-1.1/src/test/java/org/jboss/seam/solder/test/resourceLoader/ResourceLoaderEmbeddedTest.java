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

package org.jboss.seam.solder.test.resourceLoader;


import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Properties;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.resourceLoader.Resource;
import org.jboss.seam.solder.resourceLoader.ResourceLoader;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;
import static org.jboss.seam.solder.test.util.Deployments.targetContainerAdapterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(Arquillian.class)
public class ResourceLoaderEmbeddedTest {
    @Deployment(name = "ResourceLoader")
    public static Archive<?> deployment() {
        // hack to work around container differences atm
        // boolean isEmbedded = targetContainerAdapterClass().getName().contains(".embedded");
        boolean isEmbedded = true;

        WebArchive war = baseDeployment().addPackage(ResourceLoaderEmbeddedTest.class.getPackage())
                .addAsResource("com/acme/foo1")
                .addAsResource("com/acme/foo2.properties");

        if (isEmbedded) {
            war.addPackage(ResourceLoader.class.getPackage())
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        }

        return war;
    }

    @Inject
    ResourceClient resourceClient;

    @Inject
    @Resource("com/acme/foo2.properties")
    Properties foo2;

    @Inject
    BeanManager beanManager;

    @Test
    public void testLoadsStream() throws Throwable {
        InputStream stream = resourceClient.getResourceProvider().loadResourceStream("com/acme/foo1");
        assert stream != null;
        assert stream.available() > 0;
        InputStreamReader reader = new InputStreamReader(stream);
        char[] chars = new char[4];
        reader.read(chars, 0, 4);
        assert new String(chars).equals("foo1");
    }

    @Test
    public void testLoadsProperties() throws Throwable {
        assertNotNull(foo2);
        assertEquals(2, foo2.size());
        assertEquals("Pete", foo2.getProperty("name"));
        assertEquals("28", foo2.getProperty("age"));
    }

    @Test
    public void testLoadsURLs() throws Throwable {
        URL url = resourceClient.getResourceProvider().loadResource("com/acme/foo1");
        assert url != null;
        InputStream stream = url.openStream();
        assert stream.available() > 0;
        InputStreamReader reader = new InputStreamReader(stream);
        char[] chars = new char[4];
        reader.read(chars, 0, 4);
        assert new String(chars).equals("foo1");
        assert url.getFile().endsWith("/com/acme/foo1");
    }

    @Test
    public void testInitialSlashIgnored() throws Throwable {
        URL url = resourceClient.getResourceProvider().loadResource("/com/acme/foo1");
        assert url != null;
        InputStream stream = url.openStream();
        assert stream.available() > 0;
        InputStreamReader reader = new InputStreamReader(stream);
        char[] chars = new char[4];
        reader.read(chars, 0, 4);
        assert new String(chars).equals("foo1");
        assert url.getFile().endsWith("com/acme/foo1");
    }

    @Test
    public void testStreamsAreCleanedUp() throws Throwable {
        Bean<ResourceClient> bean = (Bean) beanManager.getBeans(ResourceClient.class).iterator().next();
        CreationalContext<ResourceClient> creationalContext = beanManager.createCreationalContext(bean);
        ResourceClient client = bean.create(creationalContext);
        InputStream stream = client.getResourceProvider().loadResourceStream("/com/acme/foo1");
        assert stream.available() > 0;
        InputStreamReader reader = new InputStreamReader(stream);
        char[] chars = new char[4];
        reader.read(chars, 0, 4);
        assert new String(chars).equals("foo1");
        bean.destroy(client, creationalContext);
        try {
            stream.available();
            assert false;
        } catch (IOException e) {
            // Expected
        }
    }

}
