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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.config.xml.model.ValueXmlItem;
import org.jboss.solder.properties.Property;

/**
 * class responsible for setting the value of array properties.
 *
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 */
public class ArrayFieldSet implements FieldValueObject {
    final private Property<Object> field;
    final private List<FieldValue> values;
    final private Class<?> arrayType;

    public ArrayFieldSet(Property<Object> field, List<ValueXmlItem> items) {
        this.field = field;
        this.values = new ArrayList<FieldValue>();
        arrayType = field.getJavaClass().getComponentType();
        for (ValueXmlItem i : items) {
            values.add(i.getValue());
        }

    }

    public void setValue(Object instance, CreationalContext<?> ctx, BeanManager manager) {
        try {
            Object array = Array.newInstance(arrayType, values.size());
            field.setValue(instance, array);
            for (int i = 0; i < values.size(); ++i) {
                Array.set(array, i, values.get(i).value(arrayType, ctx, manager));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
