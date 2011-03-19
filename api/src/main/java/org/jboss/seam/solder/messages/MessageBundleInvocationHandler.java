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

package org.jboss.seam.solder.messages;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;


/**
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public class MessageBundleInvocationHandler implements InvocationHandler {

    private final String projectCode;

    protected MessageBundleInvocationHandler(final String projectCode) {
        this.projectCode = projectCode;
    }

    protected MessageBundleInvocationHandler(final Class<?> type) {
        this(type.getAnnotation(MessageBundle.class));
    }

    protected MessageBundleInvocationHandler(final MessageBundle messageBundle) {
        this(messageBundle != null ? messageBundle.projectCode() : null);
    }

    public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
        final Message message = method.getAnnotation(Message.class);
        if (message == null) {
            // nothing to do...
            return null;
        }
        final Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        ArrayList<Object> newArgs = new ArrayList<Object>();
        Throwable cause = extractCause(parameterAnnotations, args, newArgs);
        String result;
        switch (message.format()) {
            case PRINTF: {
                result = String.format(getFormatString(message), newArgs.toArray());
                break;
            }
            case MESSAGE_FORMAT: {
                result = MessageFormat.format(getFormatString(message), newArgs.toArray());
                break;
            }
            default: throw new IllegalStateException();
        }
        final Class<?> returnType = method.getReturnType();
        if (Throwable.class.isAssignableFrom(returnType)) {
            // the return type is an exception
            if (cause != null) {
                final Constructor<?> constructor = returnType.getConstructor(String.class, Throwable.class);
                return constructor.newInstance(result, cause);
            } else {
                final Constructor<?> constructor = returnType.getConstructor(String.class);
                return constructor.newInstance(result);
            }
        } else {
            return result;
        }
    }

    protected String getFormatString(final Message message) {
        String formatString = message.value();
        if (formatString == null) {
            return null;
        }
        final int id = message.id();
        if (id > 0) {
            // todo - support for inherited msg id
            final String projectCode = this.projectCode;
            if (projectCode != null) {
                final StringBuilder b = new StringBuilder(32);
                b.append(projectCode).append('-');
                if (id < 10) {
                    b.append("0000");
                } else if (id < 100) {
                    b.append("000");
                } else if (id < 1000) {
                    b.append("00");
                } else if (id < 10000) {
                    b.append("0");
                }
                b.append(id);
                b.append(": ");
                b.append(formatString);
                formatString = b.toString();
            }
        }
        return formatString;
    }

    protected static Throwable extractCause(final Annotation[][] parameterAnnotations, final Object[] args, final List<Object> newArgs) {
        Throwable cause = null;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] annotations = parameterAnnotations[i];
            boolean found = false;
            for (Annotation annotation : annotations) {
                if (annotation instanceof Cause) {
                    if (cause == null) {
                        cause = (Throwable) args[i];
                    }
                    found = true;
                }
            }
            if (! found) {
                newArgs.add(args[i]);
            }
        }
        return cause;
    }
}
