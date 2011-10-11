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

import java.util.Map.Entry;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class ELFieldValueBeanTest {
    
    @Deployment(name = "ELFieldValueBeanTest")
    public static Archive<?> deployment() {
        return baseDeployment(ELFieldValueBeanTest.class, "el-set-field-value-beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addClasses(ELValueBean.class, ELValueProducer.class);
    }
    
    @Inject
    ELValueBean bean;

    @Test
    public void mapSetFieldValue() {
        Assert.assertTrue(bean.array.length == 1);
        Assert.assertEquals(bean.array[0], ELValueProducer.EL_VALUE_STRING);
        Assert.assertTrue(bean.list.size() == 1);
        Assert.assertEquals(bean.list.get(0), ELValueProducer.EL_VALUE_STRING);
        Assert.assertTrue(bean.map.size() == 1);
        Entry<String, String> entry = bean.map.entrySet().iterator().next();
        Assert.assertEquals(entry.getKey(), ELValueProducer.EL_VALUE_STRING);
        Assert.assertEquals(entry.getValue(), ELValueProducer.EL_VALUE_STRING);
    }
}
