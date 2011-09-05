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
package org.jboss.seam.solder.test.core.requires;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.test.core.requires.beans.EnabledOptionalBean;
import org.jboss.seam.solder.test.core.requires.beans.pkg.OptionalBeanWithPackageLevelDependencies;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;
import static org.junit.Assert.assertEquals;

@RunWith(Arquillian.class)
public class RequiresTest {

    @Inject
    private BeanManager manager;

    @Deployment(name = "Requires")
    public static WebArchive getDeployment() {
        return baseDeployment().addClasses(CommonInterface.class, Tiger.class, Lion.class)
                .addPackage(EnabledOptionalBean.class.getPackage())
                .addPackage(OptionalBeanWithPackageLevelDependencies.class.getPackage());
    }

    @Test
    public void testEnabledOptionalBean() {
        assertEquals(1, manager.getBeans("enabledOptionalBean").size());
    }

    @Test
    public void testDisabledOptionalBean() {
        assertEquals(0, manager.getBeans("disabledOptionalBean").size());
    }

    @Test
    public void testEnabledOptionalBeanWithFieldDependency() {
        assertEquals(1, manager.getBeans("enabledOptionalBeanWithFieldDependency").size());
    }

    @Test
    public void testDisabledOptionalBeanWithFieldDependency() {
        assertEquals(0, manager.getBeans("disabledOptionalBeanWithFieldDependency").size());
    }

    @Test
    public void testEnabledOptionalBeanWithReturnTypeDependency() {
        assertEquals(1, manager.getBeans("enabledOptionalBeanWithReturnTypeDependency").size());
    }

    @Test
    public void testDisabledOptionalBeanWithReturnTypeDependency() {
        assertEquals(0, manager.getBeans("disabledOptionalBeanWithReturnTypeDependency").size());
    }

    @Test
    public void testEnabledOptionalBeanWithSupertypeDependency() {
        assertEquals(1, manager.getBeans("enabledOptionalBeanWithSupertypeDependency").size());
    }

    @Test
    public void testDisabledOptionalBeanWithSupertypeDependency() {
        assertEquals(0, manager.getBeans("disabledOptionalBeanWithSupertypeDependency").size());
    }

    @Test
    public void testEnabledPackageLevelRequires() {
        assertEquals(4, manager.getBeans(CommonInterface.class).size());
    }

    @Test
    public void testDisabledPackageLevelRequires() {
        assertEquals(0, manager.getBeans(OptionalBeanWithPackageLevelDependencies.class).size());
    }
}
