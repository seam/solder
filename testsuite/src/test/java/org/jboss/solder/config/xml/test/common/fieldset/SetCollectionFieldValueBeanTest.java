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
package org.jboss.solder.config.xml.test.common.fieldset;

import static org.jboss.solder.config.xml.test.common.util.Deployments.baseDeployment;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SetCollectionFieldValueBeanTest {
    
    @Deployment(name = "SetCollectionFieldValueBeanTest")
    public static Archive<?> deployment() {
        return baseDeployment(SetCollectionFieldValueBeanTest.class, "colection-set-field-value-beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addClasses(CollectionFieldValue.class);
    }
    
    @Inject
    CollectionFieldValue x;

    @Test
    public void collectionSetFieldValue() {
        Assert.assertTrue(x.iset.size() == 2);
        Assert.assertTrue(x.clist.size() == 2);
        Assert.assertTrue(x.sset.size() == 2);
        boolean first = true;
        for (String i : x.sset) {
            if (first) {
                Assert.assertTrue(i.equals("1"));
                first = false;
            } else {
                Assert.assertTrue(i.equals("2"));
            }

        }
        first = true;
        for (Integer i : x.iset) {
            if (first) {
                Assert.assertTrue(i.equals(new Integer(1)));
                first = false;
            } else {
                Assert.assertTrue(i.equals(new Integer(2)));
            }

        }
        Assert.assertTrue(x.clist.get(0) == Integer.class);
        Assert.assertTrue(x.clist.get(1) == Long.class);

    }
}
