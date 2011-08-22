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
package org.jboss.seam.solder.test.el;

import javax.el.ExpressionFactory;
import javax.inject.Inject;

import com.sun.el.ExpressionFactoryImpl;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.el.ELResolverProducer;
import org.jboss.seam.solder.el.Expressions;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;
import static org.jboss.seam.solder.test.util.Deployments.targetContainerAdapterClass;

@RunWith(Arquillian.class)
public class ElTest {
    @Inject
    Expressions expressions;

    @Deployment(name = "EL")
    public static Archive<?> deployment() {
        // hack to work around container differences atm
        // boolean isEmbedded = targetContainerAdapterClass().getName().contains(".embedded");
        boolean isEmbedded = false;

        WebArchive war = baseDeployment().addPackage(ElTest.class.getPackage());
        if (isEmbedded) {
            war.addPackage(ELResolverProducer.class.getPackage())
                    // set proper EL implementation using META-INF/services/javax.el.ExpressionFactory for Weld embedded
                    .addAsServiceProvider(ExpressionFactory.class, ExpressionFactoryImpl.class)
                    .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
        }

        return war;
    }

    @Test
    public void testElResolver() {
        Assert.assertTrue(expressions.evaluateValueExpression("#{ute.speed}").equals("fast"));
        Assert.assertTrue(expressions.evaluateMethodExpression("#{ute.go}").equals(Ute.GO_STRING));
    }

    @Test
    public void testCustomElResolver() {
        Assert.assertTrue(expressions.evaluateValueExpression("#{foo}").equals("baz"));
    }

}
