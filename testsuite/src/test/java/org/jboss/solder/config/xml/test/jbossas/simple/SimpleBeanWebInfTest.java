/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the &quot;License&quot;);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an &quot;AS IS&quot; BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.solder.config.xml.test.jbossas.simple;

import java.lang.reflect.Method;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.solder.config.xml.test.common.simple.Bean1;
import org.jboss.solder.config.xml.test.common.simple.Bean2;
import org.jboss.solder.config.xml.test.common.simple.Bean3;
import org.jboss.solder.config.xml.test.common.simple.ExtendedBean;
import org.jboss.solder.config.xml.test.common.simple.ExtendedQualifier1;
import org.jboss.solder.config.xml.test.common.simple.ExtendedQualifier2;
import org.jboss.solder.config.xml.test.common.simple.OverriddenBean;
import org.jboss.solder.config.xml.test.common.simple.ScopeOverrideBean;
import org.jboss.solder.config.xml.test.common.simple.SimpleBeanTest;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.solder.config.xml.test.common.util.Deployments.baseDeploymentWebInfConfig;

/**
 * Unit test for simple App.
 */
@RunWith(Arquillian.class)
public class SimpleBeanWebInfTest {

    @Deployment(name = "SimpleBeanTestConfigInWebInf")
    public static Archive<?> deployment() {
        return baseDeploymentWebInfConfig(SimpleBeanTest.class, "simple-beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addClasses(Bean1.class, Bean2.class, Bean3.class,
                ExtendedBean.class, ExtendedQualifier1.class, ExtendedQualifier2.class,
                OverriddenBean.class, ScopeOverrideBean.class);
    }

    @Inject
    Bean1 x;

    @Inject
    Bean2 bean2;

    @Inject
    Bean3 y;

    @Test
    public void simpleBeanTest() {
        Assert.assertTrue(x != null);
        Assert.assertTrue(x.bean2 != null);

        Assert.assertEquals("test value", bean2.produceBean3);

        Assert.assertTrue(y != null);
        Assert.assertTrue("Post construct method not called", x.value == 1);
    }

    @Inject
    BeanManager manager;

    @Test
    public void testOverride() {
        Set<Bean<?>> beans = manager.getBeans(OverriddenBean.class);
        Assert.assertTrue(beans.size() == 1);
        Assert.assertTrue(beans.iterator().next().getName().equals("someBean"));

    }

    @Inject
    @ExtendedQualifier1
    @ExtendedQualifier2
    ExtendedBean ext;

    @Test
    public void testExtends() throws SecurityException, NoSuchMethodException {
        Assert.assertTrue(ext != null);
        Method method = ext.getClass().getDeclaredMethod("getData");
        method.getGenericReturnType();
    }
}
