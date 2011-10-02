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

import java.util.Collection;
import java.util.HashSet;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.ProcessProducerMethod;

import org.jboss.solder.bean.NarrowingBeanBuilder;

/**
 * Detects typed message loggers and registers a dedicated producer method for each one discovered.
 *
 * @author Pete Muir
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public class TypedMessageLoggerExtension implements Extension {
    private final Collection<AnnotatedType<?>> messageLoggerTypes;
    private Bean<Object> loggerProducerBean;

    public TypedMessageLoggerExtension() {
        this.messageLoggerTypes = new HashSet<AnnotatedType<?>>();
    }

    void detectInterfaces(@Observes ProcessAnnotatedType<?> event, BeanManager beanManager) {
        AnnotatedType<?> type = event.getAnnotatedType();
        if (type.isAnnotationPresent(MessageLogger.class)) {
            messageLoggerTypes.add(type);
        }
    }

    // according to the Java EE 6 javadoc (the authority according to the powers that be),
    // this is the correct order of type parameters
    void detectProducers(@Observes ProcessProducerMethod<Object, TypedMessageLoggerProducer> event) {
        captureProducers(event.getAnnotatedProducerMethod(), event.getBean());
    }

    // according to JSR-299 spec, this is the correct order of type parameters
    @Deprecated
    void detectProducersInverted(@Observes ProcessProducerMethod<TypedMessageLoggerProducer, Object> event) {
        captureProducers(event.getAnnotatedProducerMethod(), event.getBean());
    }

    @SuppressWarnings("unchecked")
    void captureProducers(AnnotatedMethod<?> method, Bean<?> bean) {
        if (method.isAnnotationPresent(TypedMessageLogger.class)) {
            this.loggerProducerBean = (Bean<Object>) bean;
        }
    }

    void installBeans(@Observes AfterBeanDiscovery event, BeanManager beanManager) {
        for (AnnotatedType<?> type : messageLoggerTypes) {
            event.addBean(createMessageLoggerBean(loggerProducerBean, type, beanManager));
        }
    }

    private static <T> Bean<T> createMessageLoggerBean(Bean<Object> delegate, AnnotatedType<T> type, BeanManager beanManager) {
        return new NarrowingBeanBuilder<T>(delegate, beanManager).readFromType(type).types(type.getBaseType(), Object.class).create();
    }

    void cleanup(@Observes AfterDeploymentValidation event) {
        // defensively clear the set to help with gc
        this.messageLoggerTypes.clear();
    }
}
