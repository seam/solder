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
package org.jboss.solder.reflection;

import java.lang.reflect.Method;

/**
 * Exception thrown when a annotation is created with a null value
 * for one of the members.
 *
 * @author Stuart Douglas
 */
public class NullMemberException extends RuntimeException {

    private static final long serialVersionUID = 8300345829555326883L;

    private final Class<?> annotationType;
    private final Method method;

    public NullMemberException(Class<?> annotationType, Method method, String message) {
        super(message);
        this.annotationType = annotationType;
        this.method = method;
    }

    public Class<?> getAnnotationType() {
        return annotationType;
    }

    public Method getMethod() {
        return method;
    }

}
