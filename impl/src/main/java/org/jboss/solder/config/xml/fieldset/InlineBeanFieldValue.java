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
package org.jboss.solder.config.xml.fieldset;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Field value object for an inline bean definition
 *
 * @author Stuart Douglas
 */
public class InlineBeanFieldValue implements FieldValue {

    private final int beanId;

    private final InlineBeanQualifier.InlineBeanQualifierLiteral literal;

    private Bean<?> bean;

    public InlineBeanFieldValue(int syntheticBeanQualifierNo) {
        this.beanId = syntheticBeanQualifierNo;
        this.literal = new InlineBeanQualifier.InlineBeanQualifierLiteral(beanId);
    }

    public Object value(Class<?> type, CreationalContext<?> ctx, BeanManager manager) {
        if (bean == null) {
            bean = manager.resolve(manager.getBeans(type, literal));
        }
        return manager.getReference(bean, type, ctx);
    }

}
