/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.solder.test.bean.generic.defaultbean;

import static org.jboss.solder.test.util.Deployments.baseDeployment;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class DefaultGenericBeanTest {

    @Inject
    @Foo
    private Product foo;
    
    @Inject
    @Bar
    private Product bar;
    
    @Deployment
    public static Archive<?> deployment() {
        return baseDeployment().addPackage(DefaultGenericBeanTest.class.getPackage());
    }

    @Test
    public void testOverridenGenericDefaultBean() {
        assertNotNull(foo);
        assertEquals("foo", foo.getName());
    }
    
    @Test
    public void testGenericDefaultBean() {
        assertNotNull(bar);
        assertEquals("default bar", bar.getName());
    }

}
