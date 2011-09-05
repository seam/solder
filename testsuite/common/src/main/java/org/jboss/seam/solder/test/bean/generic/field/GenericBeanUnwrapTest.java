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
package org.jboss.seam.solder.test.bean.generic.field;

import javax.inject.Inject;

import junit.framework.Assert;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;

@RunWith(Arquillian.class)
public class GenericBeanUnwrapTest {
    @Deployment(name = "GenericBeanUnwrap")
    public static Archive<?> deployment() {
        return baseDeployment().addPackage(GenericBeanUnwrapTest.class.getPackage());
    }

    @Inject
    @Foo(3)
    private Baz baz3;

    @Inject
    @Foo(3)
    private Fred fred;


    @Test
    public void testGenericUnwrap() {
        Assert.assertEquals("Hello Fred", fred.getValue());
        baz3.setFred(new Fred("Goodbye Fred"));
        Assert.assertEquals("Goodbye Fred", fred.getValue());
    }
}
