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
package org.jboss.solder.config.xml.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.NormalScope;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Scope;

import org.jboss.solder.config.xml.fieldset.FieldValueObject;
import org.jboss.solder.core.Veto;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;

public class BeanResult<X> {
    private final AnnotatedTypeBuilder<X> builder;
    private final Class<X> type;
    private final BeanResultType beanType;
    private final List<FieldValueObject> fieldValues;
    private final List<BeanResult<?>> inlineBeans;

    private final BeanManager beanManager;

    public BeanResult(Class<X> type, boolean readAnnotations, BeanResultType beanType, List<FieldValueObject> fieldValues, List<BeanResult<?>> inlineBeans, BeanManager beanManager) {
        this.beanManager = beanManager;
        this.type = type;
        builder = new AnnotatedTypeBuilder<X>().setJavaClass(type);
        builder.addToClass(XmlConfiguredBeanLiteral.INSTANCE);
        if (readAnnotations) {
            builder.readFromType(type);
            // we don't want to keep the veto annotation on the class
            builder.removeFromClass(Veto.class);
        }
        this.beanType = beanType;
        this.fieldValues = new ArrayList<FieldValueObject>(fieldValues);
        this.inlineBeans = new ArrayList<BeanResult<?>>(inlineBeans);
    }

    public List<BeanResult<?>> getInlineBeans() {
        return inlineBeans;
    }

    public Class<X> getType() {
        return type;
    }

    public BeanResultType getBeanType() {
        return beanType;
    }

    public List<FieldValueObject> getFieldValues() {
        return Collections.unmodifiableList(fieldValues);
    }

    public void addToClass(Annotation annotation) {
        // TODO: this should be done with the BeanManager one WELD-721 is resolved
        if (annotation.annotationType().isAnnotationPresent(Scope.class) || annotation.annotationType().isAnnotationPresent(NormalScope.class)) {
            // if the user is adding a new scope we need to remove any existing
            // ones
            for (Annotation typeAnnotation : type.getAnnotations()) {
                if (typeAnnotation.annotationType().isAnnotationPresent(Scope.class) || typeAnnotation.annotationType().isAnnotationPresent(NormalScope.class)) {
                    builder.removeFromClass(typeAnnotation.annotationType());
                }
            }
        }
        builder.addToClass(annotation);
    }

    public void addToField(Field field, Annotation annotation) {
        if (annotation.annotationType().isAnnotationPresent(Scope.class) || annotation.annotationType().isAnnotationPresent(NormalScope.class)) {
            for (Annotation typeAnnotation : field.getAnnotations()) {
                if (typeAnnotation.annotationType().isAnnotationPresent(Scope.class) || typeAnnotation.annotationType().isAnnotationPresent(NormalScope.class)) {
                    builder.removeFromField(field, typeAnnotation.annotationType());
                }
            }
        }
        builder.addToField(field, annotation);
    }

    public void addToMethod(Method method, Annotation annotation) {
        if (annotation.annotationType().isAnnotationPresent(Scope.class) || annotation.annotationType().isAnnotationPresent(NormalScope.class)) {
            for (Annotation typeAnnotation : method.getAnnotations()) {
                if (typeAnnotation.annotationType().isAnnotationPresent(Scope.class) || typeAnnotation.annotationType().isAnnotationPresent(NormalScope.class)) {
                    builder.removeFromMethod(method, typeAnnotation.annotationType());
                }
            }
        }
        builder.addToMethod(method, annotation);
    }

    public void addToMethodParameter(Method method, int param, Annotation annotation) {
        builder.addToMethodParameter(method, param, annotation);
    }

    public void addToConstructor(Constructor<?> constructor, Annotation annotation) {
        builder.addToConstructor((Constructor) constructor, annotation);
    }

    public void addToConstructorParameter(Constructor<?> constructor, int param, Annotation annotation) {
        builder.addToConstructorParameter((Constructor) constructor, param, annotation);
    }

    public void overrideFieldType(Field field, Class<?> javaClass) {
        builder.overrideFieldType(field, javaClass);
    }

    public AnnotatedType<?> getAnnotatedType() {
        return builder.create();
    }

}
