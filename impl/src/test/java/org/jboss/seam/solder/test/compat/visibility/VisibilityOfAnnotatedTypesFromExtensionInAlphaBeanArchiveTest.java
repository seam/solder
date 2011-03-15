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
package org.jboss.seam.solder.test.compat.visibility;

import javax.enterprise.inject.spi.Extension;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests whether an extension that observes annotated types, when put in a library with a higher order name (alpha.jar), is
 * notified of annotated types in a library with a lower order name (bravo.jar).
 * 
 * @see <a href="http://java.net/jira/browse/GLASSFISH-15735">GLASSFISH-15735</a> (unresolved)
 * 
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@RunWith(Arquillian.class)
public class VisibilityOfAnnotatedTypesFromExtensionInAlphaBeanArchiveTest {
    @Deployment
    public static Archive<?> createTestArchive() {
        JavaArchive alpha = ShrinkWrap.create(JavaArchive.class, "bravo.jar").addClasses(Soda.class)
                .addManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        JavaArchive bravo = ShrinkWrap.create(JavaArchive.class, "alpha.jar")
                .addClasses(Beer.class, AnnotatedTypeObserverExtension.class)
                .addManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addServiceProvider(Extension.class, AnnotatedTypeObserverExtension.class);

        return ShrinkWrap.create(WebArchive.class, "test.war").addWebResource(EmptyAsset.INSTANCE, "beans.xml")
                .addLibraries(alpha, bravo);
    }

    @Test
    public <X> void shouldObserveExpectedAnnotatedTypes(AnnotatedTypeObserverExtension<X> extension) {
        Assert.assertTrue(extension.observed.contains(Beer.class));
        Assert.assertTrue(extension.observed.contains(Soda.class));
    }
}