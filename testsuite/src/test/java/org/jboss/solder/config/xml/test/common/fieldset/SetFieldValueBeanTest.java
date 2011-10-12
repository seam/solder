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

import java.math.BigDecimal;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.config.xml.test.common.method.QualifierEnum;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class SetFieldValueBeanTest {
    
    @Deployment(name = "SetFieldValueBeanTest")
    public static Archive<?> deployment() {
        return baseDeployment(SetFieldValueBeanTest.class, "set-field-value-beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addClasses(FieldValueBean.class, FieldsetQualifier.class, ELValueProducer.class, QualifierEnum.class);
    }
    
    @Inject FieldValueBean x;

    @Test
    public void simpleFieldSetterTest() {
        Assert.assertTrue(x.readBigDecimalValue().compareTo(BigDecimal.TEN) == 0);
        Assert.assertTrue(x.bvalue == true);
        Assert.assertTrue(x.dvalue == 0);
        Assert.assertTrue(x.enumValue == QualifierEnum.A);
        Assert.assertTrue(x.fvalue == 0);
        Assert.assertTrue(x.getIvalue() == 11);
        Assert.assertTrue(x.lvalue == 23);
        Assert.assertTrue(x.svalue == 4);
        Assert.assertTrue(x.noFieldValue == 7);
        Assert.assertEquals(ELValueProducer.EL_VALUE_STRING, x.elValue);
        Assert.assertEquals(ELValueProducer.EL_VALUE_STRING, x.elInnerTextValue);
    }
    
    @Inject
    @FieldsetQualifier
    FieldValueBean y;

    @Test
    public void simpleShorthandFieldSetterTest() {
        Assert.assertTrue(y.readBigDecimalValue().compareTo(BigDecimal.TEN) == 0);
        Assert.assertTrue(y.bvalue == true);
        Assert.assertTrue(y.dvalue == 0);
        Assert.assertTrue(y.enumValue == QualifierEnum.A);
        Assert.assertTrue(y.fvalue == 0);
        Assert.assertTrue(y.getIvalue() == 11);
        Assert.assertTrue(y.lvalue == 23);
        Assert.assertTrue(y.svalue == 4);
        Assert.assertTrue(y.noFieldValue == 7);
    }

}
