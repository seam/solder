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
package org.jboss.seam.solder.test.compat.alternative;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import static org.junit.Assert.assertEquals;

/**
 * This test verifies that alternatives work correctly in BDAs that contain at least one extension.
 * 
 * @see <a href="http://java.net/jira/browse/GLASSFISH-15791">GLASSFISH-15791</a> (resolved)
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 */
@RunWith(Arquillian.class)
public class AlternativeTest {
    @Inject
    private Foo foo;

    @Deployment
    public static WebArchive getDeployment() {
        return create(WebArchive.class, "test.war").addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml").addAsLibrary(getJar());
    }

    public static JavaArchive getJar() {
        return create(JavaArchive.class, "test.jar")
                .addClasses(Foo.class, Bar.class, BarAlternative.class, NoopExtension.class)
                .addAsManifestResource("org/jboss/seam/solder/test/compat/alternative/beans.xml", "beans.xml")
                .addAsServiceProvider(Extension.class, NoopExtension.class);
    }

    @Test
    public void testAlternative() {
        assertEquals("barAlternative", foo.getBar().ping());
    }
}
