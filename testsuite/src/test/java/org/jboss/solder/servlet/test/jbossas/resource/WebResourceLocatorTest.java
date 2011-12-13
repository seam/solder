/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.solder.servlet.test.jbossas.resource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.solder.servlet.resource.WebResourceLocator;
import org.jboss.solder.servlet.test.weld.util.Deployments;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * Integration test for {@link WebResourceLocator}
 * 
 * @author Christian Kaltepoth
 * 
 */
@RunWith(Arquillian.class)
public class WebResourceLocatorTest {

    @Deployment
    public static Archive<?> createDeployment() {
        return Deployments.createMockableBeanWebArchive()
                .addClasses(WebResourceLocatorTest.class)
                .addAsWebResource(new StringAsset("Some web resource"), "web-resource.txt")
                .addAsWebInfResource(new StringAsset("Some file in WEB-INF directory"), "web-inf-file.txt");
    }

    @Test
    public void loadSomeWebResource() {

        WebResourceLocator locator = new WebResourceLocator();
        InputStream stream = locator.getWebResource("/web-resource.txt");

        assertNotNull("WebResourceLocator did not find the resource", stream);

        String text = readFirstLine(stream);
        assertEquals("Some web resource", text);

    }

    @Test
    public void loadResourceFromWebInfDirectory() {

        WebResourceLocator locator = new WebResourceLocator();
        InputStream stream = locator.getWebResource("/WEB-INF/web-inf-file.txt");

        assertNotNull("WebResourceLocator did not find the resource", stream);

        String text = readFirstLine(stream);
        assertEquals("Some file in WEB-INF directory", text);

    }

    @Test
    public void loadResourceThatDoesNotExist() {

        WebResourceLocator locator = new WebResourceLocator();
        InputStream stream = locator.getWebResource("/does-not-exist.txt");

        assertNull(stream);

    }

    private String readFirstLine(InputStream stream) {

        BufferedReader reader = null;
        try {

            reader = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
            return reader.readLine();

        } catch (IOException e) {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {
                    // ignore
                }
            }
        }

        return null;

    }

}
