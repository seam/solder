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
package org.jboss.solder.config.xml.test.common.method;

import static org.jboss.solder.config.xml.test.common.util.Deployments.baseDeployment;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class MethodTest {
    
    @Deployment(name = "MethodTest")
    public static Archive<?> deployment() {
        return baseDeployment(MethodTest.class, "method-beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addClasses(MethodTarget.class,
                Qualifier1.class, Qualifier2.class, Qualifier3.class,
                MethodBean.class, MethodValueBean.class, MethodValueArrayProducer.class);
    }
    
    @Inject
    MethodTarget x;

    @Test
    public void methodTest() {
        assert x != null;
        assert x.value1 == 1;
        assert x.value2 == 11;
        assert x.value3 == 11;
        assert x.longValue == 10;
    }
}
