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

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.test.util.Deployments;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Tests injection of native JBoss Logging API
 * <p/>
 * <p>
 * NOTE: Some of these tests must be verified manually as we have no way to plug
 * in a mock logger.
 * </p>
 *
 * @author David Allen
 * @author Dan Allen
 */
@RunWith(Arquillian.class)
public class LoggerInjectionTest {
    @Deployment(name = "LoggerInjection")
    public static Archive<?> createDeployment() {
        return Deployments.baseDeployment()
                .addClasses(Sparrow.class, Finch.class, Wren.class, Raven.class, NonBean.class);
    }

    @Test
    public void testLoggerInjection(Sparrow sparrow) {
        sparrow.generateLogMessage();
        Assert.assertEquals(Sparrow.class.getName(), sparrow.getLogger().getName());
    }

    @Test
    public void testLoggerInjectionWithCategory(Finch finch) {
        finch.generateLogMessage();
        Assert.assertEquals("Finch", finch.getLogger().getName());
    }

    @Test
    public void testLoggerInjectionWithTypedCategory(Wren wren) {
        wren.generateLogMessage();
        Assert.assertEquals(LoggerInjectionTest.class.getName(), wren.getLogger().getName());
    }

    @Test
    public void testLoggerInjectionWithSuffix(Raven raven) {
        raven.generateLogMessage();
        Assert.assertEquals(Raven.class.getName() + ".log", raven.getLogger().getName());
    }

    @Test
    public void testLoggerInjectionIntoNonBean(BeanManager bm) {
        NonBean nonBean = new NonBean();
        InjectionTarget<NonBean> target = bm.createInjectionTarget(bm.createAnnotatedType(NonBean.class));
        CreationalContext<NonBean> cc = bm.createCreationalContext(null);
        try {
            target.inject(nonBean, cc);
            // this will cause a NullPointerException if the injection does not occur
            nonBean.logMessage();
        } finally {
            cc.release();
        }
    }
}
