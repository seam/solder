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
package org.jboss.solder.properties.query;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * A criteria that matches a property based on its type
 *
 * @author Shane Bryzak
 * @see PropertyCriteria
 */
public class TypedPropertyCriteria implements PropertyCriteria {
    private final Class<?> propertyClass;

    public TypedPropertyCriteria(Class<?> propertyClass) {
        this.propertyClass = propertyClass;
    }

    public boolean fieldMatches(Field f) {
        return propertyClass.equals(f.getType());
    }

    public boolean methodMatches(Method m) {
        return propertyClass.equals(m.getReturnType());
    }
}
