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
package org.jboss.seam.solder.test.util;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.reflection.AnnotationInspector;
import org.jboss.seam.solder.reflection.annotated.AnnotatedTypeBuilder;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(Arquillian.class)
public class AnnotationInspectorTest {
    @Deployment(name = "AnnotationInspector")
    public static Archive<?> deployment() {
        return baseDeployment().addPackage(AnnotationInspectorTest.class.getPackage());
    }

    private static final String DOG = "Dog";
    private static final String CAT = "Cat";

    Method dogMethod;
    Annotated dogAnnotated;

    Method catMethod;
    Annotated catAnnotated;

    @Inject
    BeanManager beanManager;

    //@Before
    public void resolveMethods() throws Exception {
        dogMethod = Animals.class.getMethod("dog");
        catMethod = Animals.class.getMethod("cat");

        AnnotatedType<Animals> animals = new AnnotatedTypeBuilder<Animals>().readFromType(Animals.class).create();
        for (AnnotatedMethod<? super Animals> candidate : animals.getMethods()) {
            if (candidate.getJavaMember().getName().equals("dog")) {
                dogAnnotated = candidate;
            } else if (candidate.getJavaMember().getName().equals("cat")) {
                catAnnotated = candidate;
            }
        }
    }

    @Test
    public void testAnnotationOnElement() throws Exception {
        resolveMethods();
        assertTrue(dogMethod.isAnnotationPresent(Animal.class));

        assertTrue(AnnotationInspector.isAnnotationPresent(dogMethod, Animal.class, false, beanManager));
        assertEquals(DOG, AnnotationInspector.getAnnotation(dogMethod, Animal.class, false, beanManager).species());

        assertTrue(AnnotationInspector.isAnnotationPresent(dogMethod, Animal.class, beanManager));
        assertEquals(DOG, AnnotationInspector.getAnnotation(dogMethod, Animal.class, beanManager).species());

        assertTrue(AnnotationInspector.isAnnotationPresent(dogAnnotated, Animal.class, beanManager));
        assertEquals(DOG, AnnotationInspector.getAnnotation(dogAnnotated, Animal.class, beanManager).species());
    }

    @Test
    public void testAnnotationOnStereotype() throws Exception {
        resolveMethods();
        assertFalse(catMethod.isAnnotationPresent(Animal.class));

        assertTrue(AnnotationInspector.isAnnotationPresent(catMethod, Animal.class, true, beanManager));
        assertEquals(CAT, AnnotationInspector.getAnnotation(catMethod, Animal.class, true, beanManager).species());

        assertTrue(AnnotationInspector.isAnnotationPresent(catMethod, Animal.class, beanManager));
        assertEquals(CAT, AnnotationInspector.getAnnotation(catMethod, Animal.class, beanManager).species());

        assertTrue(AnnotationInspector.isAnnotationPresentOnStereotype(catMethod, Animal.class, beanManager));
        assertEquals(CAT, AnnotationInspector.getAnnotationFromStereotype(catMethod, Animal.class, beanManager).species());

        assertTrue(AnnotationInspector.isAnnotationPresent(catAnnotated, Animal.class, beanManager));
        assertEquals(CAT, AnnotationInspector.getAnnotation(catAnnotated, Animal.class, beanManager).species());

        assertTrue(AnnotationInspector.isAnnotationPresentOnStereotype(catAnnotated, Animal.class, beanManager));
        assertEquals(CAT, AnnotationInspector.getAnnotationFromStereotype(catAnnotated, Animal.class, beanManager).species());
    }
}
