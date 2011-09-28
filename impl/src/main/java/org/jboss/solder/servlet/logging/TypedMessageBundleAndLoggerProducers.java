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

package org.jboss.solder.servlet.logging;

import java.io.Serializable;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.solder.logging.Category;
import org.jboss.solder.logging.TypedCategory;
import org.jboss.solder.messages.Locale;

import static org.jboss.solder.logging.Logger.getMessageLogger;
import static org.jboss.solder.messages.Messages.getBundle;
import static org.jboss.solder.reflection.Reflections.getRawType;
import static org.jboss.solder.util.Locales.toLocale;

/**
 * The <code>TypedMessageBundleAndLoggerProducers</code> provides a producer method for all injected loggers and injected typed loggers.
 * <p/>
 * <strong>TEMPORARY UNTIL GLASSFISH-15735 is resolved</strong>
 *
 * @author David Allen
 * @author Pete Muir
 */
class TypedMessageBundleAndLoggerProducers implements Serializable {
    @Produces
    @TypedMessageLogger
    Object produceTypedLogger(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        if (annotated.isAnnotationPresent(Category.class)) {
            if (annotated.isAnnotationPresent(Locale.class)) {
                return getMessageLogger(getInjectionPointRawType(injectionPoint), annotated.getAnnotation(Category.class)
                        .value(), toLocale(annotated.getAnnotation(Locale.class).value()));
            } else {
                return getMessageLogger(getInjectionPointRawType(injectionPoint), annotated.getAnnotation(Category.class)
                        .value());
            }
        } else if (annotated.isAnnotationPresent(TypedCategory.class)) {
            if (annotated.isAnnotationPresent(Locale.class)) {
                return getMessageLogger(getInjectionPointRawType(injectionPoint), annotated.getAnnotation(TypedCategory.class)
                        .value().getName(), toLocale(annotated.getAnnotation(Locale.class).value()));
            } else {
                return getMessageLogger(getInjectionPointRawType(injectionPoint), annotated.getAnnotation(TypedCategory.class)
                        .value().getName());
            }
        } else {
            throw new IllegalStateException("Must specify @Category or @TypedCategory for typed loggers at [" + injectionPoint
                    + "]");
        }
    }

    @Produces
    @TypedMessageBundle
    Object produceTypedMessageBundle(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        if (annotated.isAnnotationPresent(Locale.class)) {
            return getBundle(getRawType(injectionPoint.getType()), toLocale(annotated.getAnnotation(Locale.class).value()));
        } else {
            return getBundle(getRawType(injectionPoint.getType()));
        }
    }

    private Class<?> getInjectionPointRawType(InjectionPoint injectionPoint) {
        return getRawType(injectionPoint.getType());
    }
}
