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

package org.jboss.solder.exception.control.test.weld;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.OperateOnDeployment;
import org.jboss.arquillian.container.test.api.ShouldThrowException;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.exception.control.HandlerMethod;
import org.jboss.solder.exception.control.TraversalMode;
import org.jboss.solder.exception.control.extension.CatchExtension;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.solder.exception.control.test.common.BaseWebArchive;
import org.jboss.solder.exception.control.test.common.extension.Account;
import org.jboss.solder.exception.control.test.common.extension.DecoratorAsHandler;
import org.jboss.solder.exception.control.test.common.extension.InterceptorAsHandler;
import org.jboss.solder.exception.control.test.common.extension.PretendInterceptorBinding;
import org.jboss.solder.exception.control.test.common.extension.StereotypedHandler;
import org.jboss.solder.exception.control.test.common.extension.literal.ArquillianLiteral;
import org.jboss.solder.exception.control.test.common.extension.literal.CatchQualifierLiteral;
import org.jboss.solder.exception.control.test.common.handler.BadInjectionPointHandler;
import org.jboss.solder.exception.control.test.common.handler.ExtensionExceptionHandler;
import org.jboss.weld.exceptions.DeploymentException;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;

@RunWith(Arquillian.class)
public class ExtensionTest {
    @Deployment(name = "defaultExtensionTest")
    public static Archive<?> createTestArchive() {
        return BaseWebArchive.createBase("defaultExtension")
                .addClasses(ExtensionExceptionHandler.class, StereotypedHandler.class,
                        InterceptorAsHandler.class, PretendInterceptorBinding.class, DecoratorAsHandler.class, Account.class);
    }

    @Deployment(name = "BadInjectionPointHandler")
    @ShouldThrowException(DeploymentException.class)
    public static Archive<?> createBadInjectionPointArchive() {
        return BaseWebArchive.createBase("badInjectionExtension")
                .addClasses(BadInjectionPointHandler.class);
    }

    @Inject
    CatchExtension extension;

    @Inject
    BeanManager bm;

    @Test
    @OperateOnDeployment("defaultExtensionTest")
    public void assertAnyHandlersAreFound() {
        assertFalse(extension.getHandlersForExceptionType(IllegalArgumentException.class, bm,
                Collections.<Annotation>emptySet(), TraversalMode.DEPTH_FIRST).isEmpty());
    }

    /**
     * Verifies that the expected number of handlers are found. If the extension where to scan interceptors and
     * decorators for handlers, this assertion would fail.
     *
     * @see ExtensionExceptionHandler
     * @see InterceptorAsHandler
     * @see DecoratorAsHandler
     */
    @Test
    @OperateOnDeployment("defaultExtensionTest")
    public void assertNumberOfHandlersFoundMatchesExpectedDepthFirst() {
        assertEquals(5, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm,
                Collections.<Annotation>emptySet(), TraversalMode.DEPTH_FIRST).size());
    }

    @Test
    @OperateOnDeployment("defaultExtensionTest")
    public void assertNumberOfHandlersFoundMatchesExpectedBreathFirst() {
        assertEquals(4, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm,
                Collections.<Annotation>emptySet(), TraversalMode.BREADTH_FIRST).size());
    }

    @Test
    @OperateOnDeployment("defaultExtensionTest")
    public void assertSQLHandlerFound() {
        final List<HandlerMethod<? extends Throwable>> handlerMethods = new ArrayList<HandlerMethod<? extends Throwable>>(extension.getHandlersForExceptionType(
                SQLException.class, bm, Collections.<Annotation>emptySet(), TraversalMode.DEPTH_FIRST));
        assertThat(handlerMethods.size(), is(4));
        assertThat(handlerMethods.get(3).getExceptionType(), equalTo((Type) SQLException.class));
    }

    @Test
    @OperateOnDeployment("defaultExtensionTest")
    public void assertQualifiedHandlerAndOthersAreFound() {
        HashSet<Annotation> qualifiers = new HashSet<Annotation>();
        qualifiers.add(CatchQualifierLiteral.INSTANCE);
        assertEquals(7, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers,
                TraversalMode.DEPTH_FIRST).size());
    }

    @Test
    @OperateOnDeployment("defaultExtensionTest")
    public void assertAllValidHandlersAreFoundDepthFirst() {
        HashSet<Annotation> qualifiers = new HashSet<Annotation>();
        qualifiers.add(CatchQualifierLiteral.INSTANCE);
        qualifiers.add(ArquillianLiteral.INSTANCE);
        assertEquals(8, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers,
                TraversalMode.DEPTH_FIRST).size());
    }

    @Test
    @OperateOnDeployment("defaultExtensionTest")
    public void assertAllValidHandlersAreFoundBreadthFirst() {
        HashSet<Annotation> qualifiers = new HashSet<Annotation>();
        qualifiers.add(CatchQualifierLiteral.INSTANCE);
        qualifiers.add(ArquillianLiteral.INSTANCE);
        assertEquals(4, extension.getHandlersForExceptionType(IllegalArgumentException.class, bm, qualifiers,
                TraversalMode.BREADTH_FIRST).size());
    }

    /**
     * @see BadInjectionPointHandler
     */
    @Test
    @OperateOnDeployment("BadInjectionPointHandler")
    public void assertDeploymentExceptionThrown() {
    }
}
