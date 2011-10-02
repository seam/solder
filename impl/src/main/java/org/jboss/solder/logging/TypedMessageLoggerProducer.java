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

package org.jboss.solder.logging;

import static org.jboss.solder.logging.Logger.getMessageLogger;
import static org.jboss.solder.logging.LoggerProducer.getDeclaringRawType;
import static org.jboss.solder.reflection.Reflections.getRawType;
import static org.jboss.solder.util.Locales.toLocale;

import java.io.Serializable;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.solder.messages.Locale;

/**
 * The <code>TypedMessageLoggerProducer</code> provides a producer method for all injected typed message loggers.
 * 
 * @author David Allen
 * @author Pete Muir
 */
class TypedMessageLoggerProducer implements Serializable {
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
            if (annotated.isAnnotationPresent(Locale.class)) {
                return getMessageLogger(getInjectionPointRawType(injectionPoint),
                        getDeclaringRawType(injectionPoint).getName(), toLocale(annotated.getAnnotation(Locale.class).value()));
            } else {
                return getMessageLogger(getInjectionPointRawType(injectionPoint), getDeclaringRawType(injectionPoint).getName());
            }
        }
    }

    private Class<?> getInjectionPointRawType(InjectionPoint injectionPoint) {
        return getRawType(injectionPoint.getType());
    }
}
