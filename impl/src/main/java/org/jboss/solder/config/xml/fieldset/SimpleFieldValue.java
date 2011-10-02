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
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.properties.Property;

/**
 * Field value object for a simple field
 *
 * @author Stuart Douglas
 */
public class SimpleFieldValue implements FieldValueObject {

    private final Property<Object> field;

    private final FieldValue value;

    private final Class<?> type;

    public SimpleFieldValue(Class<?> javaObject, final Property<Object> f, FieldValue value, Class<?> type) {
        this.field = f;
        this.value = value;
        this.type = type == null ? field.getJavaClass() : type;
    }

    public void setValue(Object instance, CreationalContext<?> ctx, BeanManager manager) {
        try {
            field.setValue(instance, value.value(type, ctx, manager));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
