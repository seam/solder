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

/**
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.solder.exception.control.test.common.flow;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.exception.control.ExceptionToCatch;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.solder.exception.control.test.common.BaseWebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ThrowingNewExceptionTest {
    @Deployment(name = "ThrowingNewExceptionTest")
    public static Archive<?> createTestArchive() {
        return BaseWebArchive.createBase("throwingNewException")
                .addClasses(ThrowingNewHandler.class);
    }

    @Inject
    private BeanManager bm;

    @Test(expected = UnsupportedOperationException.class)
    public void assertOutboundRethrow() {
        bm.fireEvent(new ExceptionToCatch(new NullPointerException()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void assertInboundRethrow() {
        bm.fireEvent(new ExceptionToCatch(new IllegalArgumentException()));
    }
}
