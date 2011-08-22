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

package org.jboss.seam.solder.test.logging;

import javax.enterprise.inject.Instance;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;

/**
 * Tests injection of typed message logger injections.
 * <p>
 * Also verifies that a typed message logger is permitted to extend a typed message bundle.
 * </p>
 * <p>
 * NOTE: Some of these tests must be verified manually as we have no way to plug
 * in a mock logger.
 * </p>
 *
 * @author David Allen
 * @author Dan Allen
 */
@RunWith(Arquillian.class)
public class TypedMessageLoggerInjectionTest {
    @Deployment(name = "TypedMessageLoggerInjection")
    public static Archive<?> createDeployment() {
        return baseDeployment()
                .addPackage(TypedMessageLoggerInjectionTest.class.getPackage());
    }

    @Test
    public void testMessageLoggerInjectionWithCategoryDefaulted(Instance<Owl> owlResolver) {
        owlResolver.get().generateLogMessage();
    }

    @Test
    public void testMessageLoggerInjectionWithExplicitCategory(Hawk hawk) {
        hawk.generateLogMessage();
    }

    /**
     * BaldEagle declares a passivating scope and therefore must not have any non-serializable dependencies. This test will fail
     * deployment if the type-safe logger producer is not serializable. (see SOLDER-81)
     */
    @Test
    public void testMessageLoggerInjectionOnPassivatingBean(BaldEagle baldEagle) {
        baldEagle.generateLogMessage();
    }
}
