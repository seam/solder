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

import static org.jboss.solder.logging.Logger.getLogger;
import static org.jboss.solder.reflection.Reflections.getRawType;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.solder.logging.Logger;

/**
 * The <code>LoggerProducer</code> provides a producer method for all injected loggers that use the JBoss Logging API
 * {@link Logger}.
 * 
 * @author David Allen
 * @author Pete Muir
 */
class LoggerProducer {
    @Produces
    Logger produceLog(InjectionPoint injectionPoint) {
        Annotated annotated = injectionPoint.getAnnotated();
        if (annotated.isAnnotationPresent(Category.class)) {
            if (annotated.isAnnotationPresent(Suffix.class)) {
                return getLogger(annotated.getAnnotation(Category.class).value(), annotated.getAnnotation(Suffix.class).value());
            } else {
                return getLogger(annotated.getAnnotation(Category.class).value());
            }
        } else if (annotated.isAnnotationPresent(TypedCategory.class)) {
            if (annotated.isAnnotationPresent(Suffix.class)) {
                return getLogger(annotated.getAnnotation(TypedCategory.class).value(), annotated.getAnnotation(Suffix.class)
                        .value());
            } else {
                return getLogger(annotated.getAnnotation(TypedCategory.class).value());
            }
        } else {
            if (annotated.isAnnotationPresent(Suffix.class)) {
                return getLogger(getDeclaringRawType(injectionPoint), annotated.getAnnotation(Suffix.class).value());
            } else {
                return getLogger(getDeclaringRawType(injectionPoint));
            }
        }
    }

    static Class<?> getDeclaringRawType(InjectionPoint injectionPoint) {
        if (injectionPoint.getBean() != null) {
            return getRawType(injectionPoint.getBean().getBeanClass());
        } else {
            return getRawType(injectionPoint.getMember().getDeclaringClass());
        }
    }
}
