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
package org.jboss.solder.reflection.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import org.jboss.solder.reflection.Reflections;

/**
 * Provides access to the context of an annotation redefinition.
 *
 * @author Pete Muir
 * @see AnnotatedTypeBuilder
 * @see AnnotationRedefiner
 */
public class RedefinitionContext<A extends Annotation> {

    private final AnnotatedElement annotatedElement;
    private final Type baseType;
    private final AnnotationBuilder annotationBuilder;
    private final String elementName;

    RedefinitionContext(AnnotatedElement annotatedElement, Type baseType, AnnotationBuilder annotationBuilder, String elementName) {
        this.annotatedElement = annotatedElement;
        this.baseType = baseType;
        this.annotationBuilder = annotationBuilder;
        this.elementName = elementName;
    }

    /**
     * Access to the {@link AnnotatedElement} on which this annotation is
     * defined. If the annotation is defined on a Field, this may be cast to
     * {@link Field}, if defined on a method, this may be cast to {@link Method},
     * if defined on a constructor, this may be cast to {@link Constructor}, if
     * defined on a class, this may be cast to {@link Class}, or if
     * defined on a parameter, this may be cast to {@link Parameter}
     */
    public AnnotatedElement getAnnotatedElement() {
        return annotatedElement;
    }

    /**
     * Access to the {@link Type} of the element on which this annotation is
     * defined
     */
    public Type getBaseType() {
        return baseType;
    }

    /**
     * Access to the raw type of the element on which the annotation is defined
     *
     * @return
     */
    public Class<?> getRawType() {
        return Reflections.getRawType(baseType);
    }

    /**
     * Access to the annotations present on the element. It is safe to modify the
     * annotations present using the {@link AnnotationBuilder}
     */
    public AnnotationBuilder getAnnotationBuilder() {
        return annotationBuilder;
    }

    /**
     * Access to the name of the element, or null if this represents a
     * constructor, parameter or class.
     */
    public String getElementName() {
        return elementName;
    }

}
