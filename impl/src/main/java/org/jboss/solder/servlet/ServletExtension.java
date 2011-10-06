/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
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
package org.jboss.solder.servlet;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
import javax.enterprise.inject.spi.ProcessProducerMethod;
import javax.servlet.http.Cookie;

import org.jboss.solder.bean.NarrowingBeanBuilder;
import org.jboss.solder.core.Requires;
import org.jboss.solder.literal.AnyLiteral;
import org.jboss.solder.literal.DefaultLiteral;
import org.jboss.solder.messages.Messages;
import org.jboss.solder.reflection.PrimitiveTypes;
import org.jboss.solder.servlet.http.CookieParam;
import org.jboss.solder.servlet.http.CookieParamProducer;
import org.jboss.solder.servlet.http.HeaderParam;
import org.jboss.solder.servlet.http.HeaderParamProducer;
import org.jboss.solder.servlet.http.RequestParam;
import org.jboss.solder.servlet.http.RequestParamProducer;
import org.jboss.solder.servlet.http.TemporalConverters;
import org.jboss.solder.servlet.http.TypedParamValue;
import org.jboss.solder.servlet.http.literal.CookieParamLiteral;
import org.jboss.solder.servlet.http.literal.HeaderParamLiteral;
import org.jboss.solder.servlet.http.literal.RequestParamLiteral;
import org.jboss.solder.servlet.support.ServletMessages;

/**
 * Generates producers to map to the type at an HTTP parameter injection point.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Requires("javax.servlet.Servlet")
public class ServletExtension implements Extension {
    private transient ServletMessages messages = Messages.getBundle(ServletMessages.class);

    private final Map<Class<? extends Annotation>, TypedParamProducerBlueprint> producerBlueprints;
    private final Map<Class<?>, Member> converterMembersByType;

    public ServletExtension() {
        producerBlueprints = new HashMap<Class<? extends Annotation>, TypedParamProducerBlueprint>();
        producerBlueprints.put(RequestParam.class, new TypedParamProducerBlueprint(RequestParamLiteral.INSTANCE));
        producerBlueprints.put(HeaderParam.class, new TypedParamProducerBlueprint(HeaderParamLiteral.INSTANCE));
        producerBlueprints.put(CookieParam.class, new TypedParamProducerBlueprint(CookieParamLiteral.INSTANCE));
        converterMembersByType = new HashMap<Class<?>, Member>();
    }

    public Member getConverterMember(Class<?> type) {
        return converterMembersByType.get(type);
    }

    // according to the Java EE 6 javadoc (the authority according to the powers that be),
    // this is the correct order of type parameters
    void processRequestParamProducer(@Observes ProcessProducerMethod<Object, RequestParamProducer> event) {
        if (event.getAnnotatedProducerMethod().getBaseType().equals(Object.class)
                && event.getAnnotatedProducerMethod().isAnnotationPresent(TypedParamValue.class)) {
            producerBlueprints.get(RequestParam.class).setProducer(event.getBean());
        }
    }

    // according to JSR-299 spec, this is the correct order of type parameters
    @Deprecated
    void processRequestParamProducerInverted(@Observes ProcessProducerMethod<RequestParamProducer, Object> event) {
        if (isTypedParamProducer(event.getAnnotatedProducerMethod())) {
            producerBlueprints.get(RequestParam.class).setProducer(event.getBean());
        }
    }

    // according to the Java EE 6 javadoc (the authority according to the powers that be),
    // this is the correct order of type parameters
    void processHeaderParamProducer(@Observes ProcessProducerMethod<Object, HeaderParamProducer> event) {
        if (isTypedParamProducer(event.getAnnotatedProducerMethod())) {
            producerBlueprints.get(HeaderParam.class).setProducer(event.getBean());
        }
    }

    // according to JSR-299 spec, this is the correct order of type parameters
    @Deprecated
    void processHeaderParamProducerInverted(@Observes ProcessProducerMethod<HeaderParamProducer, Object> event) {
        if (isTypedParamProducer(event.getAnnotatedProducerMethod())) {
            producerBlueprints.get(HeaderParam.class).setProducer(event.getBean());
        }
    }

    // according to the Java EE 6 javadoc (the authority according to the powers that be),
    // this is the correct order of type parameters
    void processCookieParamProducer(@Observes ProcessProducerMethod<Object, CookieParamProducer> event) {
        if (isTypedParamProducer(event.getAnnotatedProducerMethod())) {
            producerBlueprints.get(CookieParam.class).setProducer(event.getBean());
        }
    }

    // according to JSR-299 spec, this is the correct order of type parameters
    @Deprecated
    void processCookieParamProducerInverted(@Observes ProcessProducerMethod<CookieParamProducer, Object> event) {
        if (isTypedParamProducer(event.getAnnotatedProducerMethod())) {
            producerBlueprints.get(CookieParam.class).setProducer(event.getBean());
        }
    }

    <X> void detectInjections(@Observes ProcessInjectionTarget<X> event) {
        for (InjectionPoint ip : event.getInjectionTarget().getInjectionPoints()) {
            Annotated annotated = ip.getAnnotated();
            for (Class<? extends Annotation> paramAnnotationType : producerBlueprints.keySet()) {
                if (annotated.isAnnotationPresent(paramAnnotationType)) {
                    Collection<Annotation> allowed = Arrays.asList(DefaultLiteral.INSTANCE, AnyLiteral.INSTANCE,
                            annotated.getAnnotation(paramAnnotationType));
                    boolean error = false;
                    for (Annotation q : ip.getQualifiers()) {
                        if (!allowed.contains(q)) {
                            event.addDefinitionError(new IllegalArgumentException(messages.additionalQualifiersNotPermitted(
                                    paramAnnotationType.getSimpleName(), ip)));
                            error = true;
                            break;
                        }
                    }
                    if (error) {
                        break;
                    }
                    Type targetType = getActualBeanType(ip.getType());
                    if (!(targetType instanceof Class)) {
                        event.addDefinitionError(new IllegalArgumentException(messages.rawTypeRequired(
                                paramAnnotationType.getSimpleName(), ip)));
                        break;
                    }
                    try {
                        Class<?> targetClass = (Class<?>) targetType;
                        if (targetClass.equals(String.class)
                                || (paramAnnotationType.equals(CookieParam.class) && targetClass.equals(Cookie.class))) {
                            // no converter needed
                        } else {
                            targetClass = PrimitiveTypes.box(targetClass);
                            Member converter = null;

                            if (targetClass.isEnum()) {
                                converter = targetClass.getMethod("valueOf", String.class);
                            } else if (Date.class.isAssignableFrom(targetClass)) {
                                converter = TemporalConverters.class.getMethod("parseDate", String.class);
                            } else if (Calendar.class.isAssignableFrom(targetClass)) {
                                converter = TemporalConverters.class.getMethod("parseCalendar", String.class);
                            } else {
                                try {
                                    converter = targetClass.getConstructor(String.class);
                                } catch (NoSuchMethodException sce) {
                                    converter = targetClass.getMethod("valueOf", String.class);
                                }
                            }

                            // TODO need way to register or detect custom converters
                            converterMembersByType.put(targetClass, converter);
                        }
                        producerBlueprints.get(paramAnnotationType).addTargetType(targetClass);
                    } catch (NoSuchMethodException nme) {
                        event.addDefinitionError(new IllegalArgumentException(messages.noConverterForType(
                                paramAnnotationType.getSimpleName(), ip)));
                    }
                }
            }
        }
    }

    void installBeans(@Observes AfterBeanDiscovery event, BeanManager beanManager) {
        for (TypedParamProducerBlueprint blueprint : producerBlueprints.values()) {
            if (blueprint.getProducer() != null) {
                for (Class<?> type : blueprint.getTargetTypes()) {
                    event.addBean(createTypedParamProducer(blueprint.getProducer(), type, blueprint.getQualifier(), beanManager));
                }
            }
        }

        producerBlueprints.clear();
    }

    private boolean isTypedParamProducer(AnnotatedMethod<?> method) {
        return method.getBaseType().equals(Object.class) && method.isAnnotationPresent(TypedParamValue.class);
    }

    private <T> Bean<T> createTypedParamProducer(Bean<Object> delegate, Class<T> targetType, Annotation qualifier,
                                                 BeanManager beanManager) {
        return new NarrowingBeanBuilder<T>(delegate, beanManager).readFromType(beanManager.createAnnotatedType(targetType))
                .qualifiers(qualifier).create();
    }

    private Type getActualBeanType(Type t) {
        if (t instanceof ParameterizedType && ((ParameterizedType) t).getRawType().equals(Instance.class)) {
            return ((ParameterizedType) t).getActualTypeArguments()[0];
        }
        return t;
    }

    public static class TypedParamProducerBlueprint {
        private Bean<Object> producer;
        private Set<Class<?>> targetTypes;
        private Annotation qualifier;

        public TypedParamProducerBlueprint(Annotation qualifier) {
            this.qualifier = qualifier;
            targetTypes = new HashSet<Class<?>>();
        }

        public Bean<Object> getProducer() {
            return producer;
        }

        // unchecked operation to support inverted observer type params
        @SuppressWarnings({"rawtypes", "unchecked"})
        public void setProducer(Bean producer) {
            this.producer = producer;
        }

        public Set<Class<?>> getTargetTypes() {
            return targetTypes;
        }

        public void addTargetType(Class<?> targetType) {
            targetTypes.add(targetType);
        }

        public Annotation getQualifier() {
            return qualifier;
        }
    }
}
