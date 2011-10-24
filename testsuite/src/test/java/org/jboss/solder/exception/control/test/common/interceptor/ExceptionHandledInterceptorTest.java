/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.solder.exception.control.test.common.interceptor;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.solder.exception.control.test.common.BaseWebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

/**
 * Tests for ExceptionHandledInterceptor.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 */
@RunWith(Arquillian.class)
public class ExceptionHandledInterceptorTest {

    @Inject
    private Ping ping;

    @Deployment(name = "ExceptionHandledInterceptorTest")
    public static Archive<?> createTestArchive() {
        return BaseWebArchive.createBase(false).addPackage(ExceptionHandledInterceptorTest.class.getPackage())
                .addAsWebInfResource("org/jboss/solder/exception/control/test/common/interceptor/beans.xml", "beans.xml");
    }

    @Test(expected = CheckedException.class)
    public void testRethrownExceptionNotWrapped() throws Exception {
        ping.ping();
    }

    @Test(expected = ClassNotFoundException.class)
    public void testExceptionRethrownWhenNoExceptionHandlerAvailable() throws Exception {
        ping.pong();
    }

    @Test
    public void testDefaultValuesReturnedByHandledMethod(PrimitiveValues values) {
        assertEquals(0, values.getByte());
        assertEquals(0, values.getShort());
        assertEquals(0, values.getInt());
        assertEquals(0l, values.getLong());
        assertEquals(0.0f, values.getFloat(), 0.0f);
        assertEquals(0.0d, values.getDouble(), 0.0d);
        assertEquals('\u0000', values.getChar());
        assertEquals(false, values.getBoolean());
    }
}
