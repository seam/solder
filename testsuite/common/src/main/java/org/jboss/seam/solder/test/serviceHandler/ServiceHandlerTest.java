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
package org.jboss.seam.solder.test.serviceHandler;

import javax.inject.Inject;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;

/**
 * @author Stuart Douglas
 */
@RunWith(Arquillian.class)
public class ServiceHandlerTest {

    @Deployment(name = "ServiceHandler")
    public static Archive<?> deployment() {
        return baseDeployment().addPackage(ServiceHandlerTest.class.getPackage());
    }

    @Inject
    private HelloWorld helloWorld;

    @Inject
    private GoodbyeWorld goodbyeWorld;

    @Inject
    private DecoratedHelloWorld decoratedHelloWorld;

    @Test
    public void testProxiedInterface() {
        Assert.assertTrue(helloWorld.helloWorld().equals("helloWorld"));
    }

    @Test
    public void testProxiedAbstractClass() {
        Assert.assertTrue(goodbyeWorld.goodbyeWorld().equals("goodbyeWorld"));
        Assert.assertFalse(goodbyeWorld.otherMethod().equals("otherMethod"));
    }

    @Test
    public void testInjectionIntoHandler() {
        Assert.assertTrue(decoratedHelloWorld.decoratedHelloWorld().equals("-decoratedHelloWorld-"));
    }

}
