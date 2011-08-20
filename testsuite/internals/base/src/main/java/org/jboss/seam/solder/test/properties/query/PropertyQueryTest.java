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
package org.jboss.seam.solder.test.properties.query;

import org.jboss.seam.solder.properties.Property;
import org.jboss.seam.solder.properties.query.NamedPropertyCriteria;
import org.jboss.seam.solder.properties.query.PropertyQueries;
import org.jboss.seam.solder.properties.query.PropertyQuery;
import org.jboss.seam.solder.properties.query.TypedPropertyCriteria;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Validate the property query mechanism.
 *
 * @author Dan Allen
 */
public class PropertyQueryTest {
    /**
     * Querying for a single result with a criteria that matches multiple
     * properties should throw an exception.
     *
     * @see PropertyQuery#getSingleResult()
     */
    @Test(expected = RuntimeException.class)
    public void testNonUniqueSingleResultThrowsException() {
        PropertyQuery<String> q = PropertyQueries.<String>createQuery(Person.class);
        q.addCriteria(new TypedPropertyCriteria(String.class));
        q.getSingleResult();
    }

    /**
     * Querying for a single result with a criteria that does not match
     * any properties should throw an exception.
     *
     * @see PropertyQuery#getSingleResult()
     */
    @Test(expected = RuntimeException.class)
    public void testEmptySingleResultThrowsException() {
        PropertyQuery<String> q = PropertyQueries.<String>createQuery(Person.class);
        q.addCriteria(new TypedPropertyCriteria(Integer.class));
        q.getSingleResult();
    }

    /**
     * Querying for a single result with a criterai that matches exactly one
     * property should return the property.
     *
     * @see PropertyQuery#getSingleResult()
     */
    @Test
    public void testSingleResult() {
        PropertyQuery<String> q = PropertyQueries.<String>createQuery(Person.class);
        q.addCriteria(new NamedPropertyCriteria("name"));
        Property<String> p = q.getSingleResult();
        assertNotNull(p);
        Person o = new Person();
        o.setName("Trap");
        assertEquals("Trap", p.getValue(o));
    }
}
