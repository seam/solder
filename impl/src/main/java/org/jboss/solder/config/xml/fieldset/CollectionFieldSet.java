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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.config.xml.model.ValueXmlItem;
import org.jboss.solder.config.xml.util.TypeReader;
import org.jboss.solder.properties.Property;

/**
 * class responsible for setting the value of collection properties.
 * <p/>
 * It can deal with the following collection types: -Set -List -Collection
 * -SortedSet -HashSet -ArrayList -TreeSet -LinkedList
 *
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 */
public class CollectionFieldSet implements FieldValueObject {
    private final Property<Object> field;
    private final List<FieldValue> values;
    private final Class<?> elementType;
    private final Class<? extends Collection> collectionType;

    public CollectionFieldSet(Property<Object> field, List<ValueXmlItem> items) {
        this.field = field;
        this.values = new ArrayList<FieldValue>();

        Type type = field.getBaseType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            if (parameterizedType.getRawType() == Collection.class) {
                collectionType = LinkedHashSet.class;
            } else if (parameterizedType.getRawType() == List.class) {
                collectionType = ArrayList.class;
            } else if (parameterizedType.getRawType() == Set.class) {
                collectionType = LinkedHashSet.class;
            } else if (parameterizedType.getRawType() == SortedSet.class) {
                collectionType = TreeSet.class;
            } else if (parameterizedType.getRawType() == HashSet.class) {
                collectionType = HashSet.class;
            } else if (parameterizedType.getRawType() == ArrayList.class) {
                collectionType = ArrayList.class;
            } else if (parameterizedType.getRawType() == LinkedList.class) {
                collectionType = LinkedList.class;
            } else if (parameterizedType.getRawType() == LinkedHashSet.class) {
                collectionType = LinkedHashSet.class;
            } else if (parameterizedType.getRawType() == TreeSet.class) {
                collectionType = TreeSet.class;
            } else {
                throw new RuntimeException("Could not determine element type for " + field.getDeclaringClass().getName() + "." + field.getName());
            }
            elementType = TypeReader.readClassFromType(parameterizedType.getActualTypeArguments()[0]);
        } else {
            throw new RuntimeException("Could not determine element type for " + field.getDeclaringClass().getName() + "." + field.getName());
        }

        for (ValueXmlItem i : items) {
            values.add(i.getValue());
        }
    }

    public void setValue(Object instance, CreationalContext<?> ctx, BeanManager manager) {
        try {
            Collection<Object> res = collectionType.newInstance();
            field.setValue(instance, res);
            for (int i = 0; i < values.size(); ++i) {
                res.add(values.get(i).value(elementType, ctx, manager));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
