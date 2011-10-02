/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.solder.exception.control;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionPoint;

import org.jboss.solder.bean.Beans;
import org.jboss.solder.bean.ImmutableInjectionPoint;
import org.jboss.solder.literal.AnyLiteral;
import org.jboss.solder.reflection.annotated.InjectableMethod;
import org.jboss.solder.exception.control.CaughtException;
import org.jboss.solder.exception.control.HandlerMethod;
import org.jboss.solder.exception.control.Handles;
import org.jboss.solder.exception.control.TraversalMode;

/**
 * Implementation of {@link org.jboss.solder.exception.control.HandlerMethod}.
 *
 * @param <T> Type of the exception this handler handles.
 */
public class HandlerMethodImpl<T extends Throwable> implements HandlerMethod<T> {
    private final Class<?> beanClass;
    private Bean<?> bean;
    private final Set<Annotation> qualifiers;
    private final Type exceptionType;
    private final AnnotatedMethod<?> handler;
    private final TraversalMode traversalMode;
    private final int precedence;
    private final Method javaMethod;
    private final AnnotatedParameter<?> handlerParameter;
    private final Set<InjectionPoint> injectionPoints;

    /**
     * Determines if the given method is a handler by looking for the {@link Handles} annotation on a parameter.
     *
     * @param method method to search
     * @return true if {@link Handles} is found, false otherwise
     */
    public static boolean isHandler(final AnnotatedMethod<?> method) {
        if (method == null) {
            throw new IllegalArgumentException("Method must not be null");
        }

        for (AnnotatedParameter<?> param : method.getParameters()) {
            if (param.isAnnotationPresent(Handles.class)) {
                return true;
            }
        }

        return false;
    }

    public static AnnotatedParameter<?> findHandlerParameter(final AnnotatedMethod<?> method) {
        if (!isHandler(method)) {
            throw new IllegalArgumentException("Method is not a valid handler");
        }

        AnnotatedParameter<?> returnParam = null;

        for (AnnotatedParameter<?> param : method.getParameters()) {
            if (param.isAnnotationPresent(Handles.class)) {
                returnParam = param;
                break;
            }
        }

        return returnParam;
    }

    /**
     * Sole Constructor.
     *
     * @param method found handler
     * @param bm     active BeanManager
     * @throws IllegalArgumentException if method is null, has no params or first param is not annotated with
     *                                  {@link Handles}
     */
    public HandlerMethodImpl(final AnnotatedMethod<?> method, final BeanManager bm) {
        if (!HandlerMethodImpl.isHandler(method)) {
            throw new IllegalArgumentException(MessageFormat.format("{0} is not a valid handler", method));
        }

        final Set<Annotation> tmpQualifiers = new HashSet<Annotation>();

        this.handler = method;
        this.javaMethod = method.getJavaMember();

        this.handlerParameter = findHandlerParameter(method);

        if (!this.handlerParameter.isAnnotationPresent(Handles.class)) {
            throw new IllegalArgumentException("Method is not annotated with @Handles");
        }

        this.traversalMode = this.handlerParameter.getAnnotation(Handles.class).during();
        this.precedence = this.handlerParameter.getAnnotation(Handles.class).precedence();
        tmpQualifiers.addAll(Beans.getQualifiers(bm, this.handlerParameter.getAnnotations()));

        if (tmpQualifiers.isEmpty()) {
            tmpQualifiers.add(AnyLiteral.INSTANCE);
        }

        this.qualifiers = tmpQualifiers;
        this.beanClass = method.getJavaMember().getDeclaringClass();
        this.exceptionType = ((ParameterizedType) this.handlerParameter.getBaseType()).getActualTypeArguments()[0];
        this.injectionPoints = new HashSet<InjectionPoint>(method.getParameters().size() - 1);

        for (AnnotatedParameter<?> param : method.getParameters()) {
            if (!param.equals(this.handlerParameter))
                this.injectionPoints.add(new ImmutableInjectionPoint(param, bm, this.getBean(bm), false, false));
        }
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    /**
     * {@inheritDoc}
     */
    public synchronized Bean<?> getBean(BeanManager bm) {
        if (this.bean == null) {
            this.bean = bm.resolve(bm.getBeans(this.beanClass));
        }
        return this.bean;
    }

    /**
     * {@inheritDoc}
     */
    public Set<Annotation> getQualifiers() {
        return Collections.unmodifiableSet(this.qualifiers);
    }

    /**
     * {@inheritDoc}
     */
    public Type getExceptionType() {
        return this.exceptionType;
    }

    /**
     * {@inheritDoc}
     */
    public void notify(final CaughtException<T> event, final BeanManager bm) {
        CreationalContext<?> ctx = null;
        try {
            ctx = bm.createCreationalContext(null);
            Object handlerInstance = bm.getReference(this.getBean(bm), this.beanClass, ctx);
            InjectableMethod<?> im = createInjectableMethod(this.handler, this.getBean(bm), bm);
            im.invoke(handlerInstance, ctx, new OutboundParameterValueRedefiner(event, bm, this));
        } finally {
            if (ctx != null) {
                ctx.release();
            }
        }
    }

    private <X> InjectableMethod<X> createInjectableMethod(AnnotatedMethod<X> handlerMethod, Bean<?> bean, BeanManager manager) {
        return new InjectableMethod<X>(handlerMethod, bean, manager);
    }

    /**
     * {@inheritDoc}
     */
    public TraversalMode getTraversalMode() {
        return this.traversalMode;
    }

    /**
     * {@inheritDoc}
     */
    public int getPrecedence() {
        return this.precedence;
    }

    /**
     * {@inheritDoc}
     */
    public Method getJavaMethod() {
        return this.javaMethod;
    }

    public AnnotatedParameter<?> getHandlerParameter() {
        return this.handlerParameter;
    }

    public Set<InjectionPoint> getInjectionPoints() {
        return new HashSet<InjectionPoint>(this.injectionPoints);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HandlerMethod<?> that = (HandlerMethod<?>) o;

        if (!qualifiers.equals(that.getQualifiers())) {
            return false;
        }
        if (!exceptionType.equals(that.getExceptionType())) {
            return false;
        }
        if (precedence != that.getPrecedence()) {
            return false;
        }

        return traversalMode == that.getTraversalMode();

    }

    @Override
    public int hashCode() {
        int result = beanClass.hashCode();
        result = 5 * result + qualifiers.hashCode();
        result = 5 * result + exceptionType.hashCode();
        result = 5 * result + traversalMode.hashCode();
        result = 5 * result + precedence;
        result = 5 * result + javaMethod.hashCode();
        result = 5 * result + handlerParameter.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder("Qualifiers: ").append(this.qualifiers).append(" ")
                .append("TraversalMode: ").append(this.traversalMode).append(" ")
                .append("Handles Type: ").append(this.exceptionType).append(" ")
                .append("Precedence: ").append(this.precedence).append(" ")
                .append(this.handler.toString()).toString();
    }
}
