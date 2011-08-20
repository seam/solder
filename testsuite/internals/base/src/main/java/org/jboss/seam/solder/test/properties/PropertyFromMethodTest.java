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
package org.jboss.seam.solder.test.properties;

import java.lang.reflect.Method;
import java.net.URL;

import org.jboss.seam.solder.properties.Properties;
import org.jboss.seam.solder.properties.Property;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Verify that only valid properties are permitted, as per the JavaBean specification.
 *
 * @author Dan Allen
 * @see http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html
 */
public class PropertyFromMethodTest {
    @Test
    public void testValidPropertyGetterMethod() throws Exception {
        Method getter = ClassToIntrospect.class.getMethod("getName");
        Property<String> p = Properties.createProperty(getter);
        assertNotNull(p);
        assertEquals("name", p.getName());
        assertEquals(getter, p.getMember());
    }

    @Test
    public void testValidPropertySetterMethod() throws Exception {
        Property<String> p = Properties.createProperty(ClassToIntrospect.class.getMethod("setName", String.class));
        assertNotNull(p);
        assertEquals("name", p.getName());
    }

    @Test
    public void testReadOnlyProperty() throws Exception {
        Property<String> p = Properties.createProperty(ClassToIntrospect.class.getMethod("getTitle"));
        assertNotNull(p);
        assertEquals("title", p.getName());
        assertTrue(p.isReadOnly());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyPropertyGetterMethod() throws Exception {
        Properties.createProperty(ClassToIntrospect.class.getMethod("get"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyBooleanPropertyGetterMethod() throws Exception {
        Properties.createProperty(ClassToIntrospect.class.getMethod("is"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNonPrimitiveBooleanPropertyIsMethod() throws Exception {
        Properties.createProperty(ClassToIntrospect.class.getMethod("isValid"));
    }

    @Test
    public void testSingleCharPropertyGetterMethod() throws Exception {
        Method getter = ClassToIntrospect.class.getMethod("getP");
        Property<String> p = Properties.createProperty(getter);
        assertNotNull(p);
        assertEquals("p", p.getName());
        assertEquals(getter, p.getMember());
    }

    @Test
    public void testSingleCharPropertySetterMethod() throws Exception {
        Property<String> p = Properties.createProperty(ClassToIntrospect.class.getMethod("setP", String.class));
        assertNotNull(p);
        assertEquals("p", p.getName());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetterMethodWithVoidReturnType() throws Exception {
        Properties.createProperty(ClassToIntrospect.class.getMethod("getFooBar"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testSetterMethodWithMultipleParameters() throws Exception {
        Properties.createProperty(ClassToIntrospect.class.getMethod("setSalary", Double.class, Double.class));
    }

    @Test
    public void testAcronymProperty() throws Exception {
        Method getter = ClassToIntrospect.class.getMethod("getURL");
        Property<URL> p = Properties.createProperty(getter);
        assertNotNull(p);
        assertEquals("URL", p.getName());
        assertEquals(getter, p.getMember());
    }
}
