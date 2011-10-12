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
package org.jboss.solder.config.xml.test.common.injection;

import static org.jboss.solder.config.xml.test.common.util.Deployments.baseDeployment;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.config.xml.test.common.method.QualifiedType;
import org.jboss.solder.config.xml.test.common.method.QualifierEnum;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Test that XML configured Qualifiers work as expected
 */
@RunWith(Arquillian.class)
public class QualifierAttributesTest {
    
    @Deployment(name = "QualifierAttributesTest")
    public static Archive<?> deployment() {
        return baseDeployment(QualifierAttributesTest.class, "qualifier-attributes-test-beans.xml")
            .addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
            .addClasses(QualifierTestBean.class, QualifiedType.class, QualifiedBean1.class, QualifiedBean2.class, 
                    OtherQualifier.class, QualifierEnum.class);
    }
    
    @Inject
    QualifierTestBean x;

    @Test()
    public void testQualifiersWithAttributes() {
        Assert.assertTrue(x.bean1.getBeanNumber() == 1);
        Assert.assertTrue(x.bean2.getBeanNumber() == 2);
    }
}
