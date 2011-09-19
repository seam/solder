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
package org.jboss.solder.support;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.jboss.solder.messages.Message;
import org.jboss.solder.messages.MessageBundle;

/**
 * Messages used for exception messages in Solder.
 *
 * @author Pete Muir
 */
@MessageBundle
public interface SolderMessages {
    @Message("annotationType %s already present")
    public String annotationAlreadyPresent(Class<? extends Annotation> annotationType);

    @Message("annotationType %s not present")
    public String annotationNotPresent(Class<? extends Annotation> annotationType);

    @Message("field %s not present on class %s")
    public String fieldNotPresent(Field field, Class<?> declaringClass);

    @Message("method %s not present on class %s")
    public String methodNotPresent(Method method, Class<?> declaringClass);

    @Message("parameter %s not present on method %s declared on class %s")
    public String parameterNotPresent(Method method, int parameterPosition, Class<?> declaringClass);

    @Message("%s parameter must not be null")
    public String parameterMustNotBeNull(String parameterName);
}
