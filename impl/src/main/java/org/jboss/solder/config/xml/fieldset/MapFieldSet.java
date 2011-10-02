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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.config.xml.model.EntryXmlItem;
import org.jboss.solder.config.xml.util.TypeReader;
import org.jboss.solder.properties.Property;

/**
 * class responsible for setting the value of map properties.
 *
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 */
public class MapFieldSet implements FieldValueObject {
    private final Property<Object> field;
    private final List<Entry<FieldValue, FieldValue>> values;
    private final Class<?> keyType;
    private final Class<?> valueType;
    private final Class<? extends Map> collectionType;

    public MapFieldSet(Property<Object> field, List<EntryXmlItem> items) {
        this.field = field;
        this.values = new ArrayList<Entry<FieldValue, FieldValue>>();
        // figure out the collection type
        Type type = field.getBaseType();
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) type;

            if (parameterizedType.getRawType() == Map.class) {
                collectionType = LinkedHashMap.class;
            } else if (parameterizedType.getRawType() == LinkedHashMap.class) {
                collectionType = LinkedHashMap.class;
            } else if (parameterizedType.getRawType() == HashMap.class) {
                collectionType = HashMap.class;
            } else if (parameterizedType.getRawType() == SortedMap.class) {
                collectionType = TreeMap.class;
            } else if (parameterizedType.getRawType() == TreeMap.class) {
                collectionType = TreeMap.class;
            } else {
                throw new RuntimeException("Could not determine element type for map " + field.getDeclaringClass().getName() + "." + field.getName());
            }

            keyType = TypeReader.readClassFromType(parameterizedType.getActualTypeArguments()[0]);
            valueType = TypeReader.readClassFromType(parameterizedType.getActualTypeArguments()[1]);
        } else if (type == Properties.class) {
            collectionType = Properties.class;
            keyType = TypeReader.readClassFromType(String.class);
            valueType = TypeReader.readClassFromType(String.class);
        } else {
            throw new RuntimeException("Could not determine element type for map " + field.getDeclaringClass().getName() + "." + field.getName());
        }

        for (EntryXmlItem i : items) {
            values.add(new EntryImpl(i.getKey().getValue(), i.getValue().getValue()));
        }
    }

    public void setValue(Object instance, CreationalContext<?> ctx, BeanManager manager) {
        try {
            Map res = collectionType.newInstance();
            field.setValue(instance, res);
            for (int i = 0; i < values.size(); ++i) {
                Entry<FieldValue, FieldValue> e = values.get(i);
                res.put(e.getKey().value(keyType, ctx, manager), e.getValue().value(valueType, ctx, manager));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private final class EntryImpl implements Entry<FieldValue, FieldValue> {
        private FieldValue key;
        private FieldValue value;

        public EntryImpl(FieldValue key, FieldValue value) {
            this.key = key;
            this.value = value;
        }

        public FieldValue getKey() {
            return key;
        }

        public FieldValue getValue() {
            return value;
        }

        public FieldValue setValue(FieldValue value) {
            return this.value = value;
        }

        public void setKey(FieldValue key) {
            this.key = key;
        }
    }
}
