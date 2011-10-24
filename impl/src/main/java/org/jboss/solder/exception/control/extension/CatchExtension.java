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

package org.jboss.solder.exception.control.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.InjectionException;
import javax.enterprise.inject.spi.AfterDeploymentValidation;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Decorator;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.ProcessBean;

import org.jboss.solder.exception.control.log.CatchExtensionLog;
import org.jboss.solder.literal.AnyLiteral;
import org.jboss.solder.logging.Logger;
import org.jboss.solder.reflection.AnnotationInspector;
import org.jboss.solder.reflection.HierarchyDiscovery;
import org.jboss.solder.exception.control.ExceptionHandlerComparator;
import org.jboss.solder.exception.control.HandlerMethod;
import org.jboss.solder.exception.control.HandlerMethodContainer;
import org.jboss.solder.exception.control.HandlerMethodImpl;
import org.jboss.solder.exception.control.HandlesExceptions;
import org.jboss.solder.exception.control.TraversalMode;

/**
 * CDI extension to find handlers at startup.
 */
@SuppressWarnings("unchecked")
public class CatchExtension implements Extension, HandlerMethodContainer {
    private final Map<? super Type, Collection<HandlerMethod<? extends Throwable>>> allHandlers;

    private final CatchExtensionLog log = Logger.getMessageLogger(CatchExtensionLog.class, CatchExtension.class.getPackage().getName());

    public CatchExtension() {
        this.allHandlers = new HashMap<Type, Collection<HandlerMethod<? extends Throwable>>>();
    }

    /**
     * Listener to ProcessBean event to locate handlers.
     *
     * @param pmb Event from CDI SPI
     * @param bm  Activated Bean Manager
     * @throws TypeNotPresentException if any of the actual type arguments refers to a non-existent type declaration when
     *                                 trying to obtain the actual type arguments from a {@link ParameterizedType}
     * @throws java.lang.reflect.MalformedParameterizedTypeException
     *                                 if any of the actual type parameters refer to a parameterized type that cannot be
     *                                 instantiated for any reason when trying to obtain the actual type arguments from a
     *                                 {@link ParameterizedType}
     */
    public <T> void findHandlers(@Observes final ProcessBean<?> pmb, final BeanManager bm) {
        if (!(pmb.getAnnotated() instanceof AnnotatedType) || pmb.getBean() instanceof Interceptor ||
                pmb.getBean() instanceof Decorator) {
            return;
        }

        final AnnotatedType<T> type = (AnnotatedType<T>) pmb.getAnnotated();

        if (AnnotationInspector.isAnnotationPresent(type, HandlesExceptions.class, bm)) {
            final Set<AnnotatedMethod<? super T>> methods = type.getMethods();

            for (AnnotatedMethod<? super T> method : methods) {
                if (HandlerMethodImpl.isHandler(method)) {
                    final AnnotatedParameter<?> param = HandlerMethodImpl.findHandlerParameter(method);
                    if (method.getJavaMember().getExceptionTypes().length != 0) {
                        pmb.addDefinitionError(new IllegalArgumentException(
                                String.format("Handler method %s must not throw exceptions", method.getJavaMember())));
                    }
                    final Class<? extends Throwable> exceptionType = (Class<? extends Throwable>) ((ParameterizedType) param.getBaseType()).getActualTypeArguments()[0];

                    registerHandlerMethod(new HandlerMethodImpl(method, bm));
                }
            }
        }
    }

    /**
     * Verifies all injection points for every handler are valid.
     *
     * @param adv Lifecycle event
     * @param bm  BeanManager instance
     */
    public void verifyInjectionPoints(@Observes final AfterDeploymentValidation adv, final BeanManager bm) {
        for (Map.Entry<? super Type, Collection<HandlerMethod<? extends Throwable>>> entry : this.allHandlers.entrySet()) {
            for (HandlerMethod<? extends Throwable> handler : entry.getValue()) {
                for (InjectionPoint ip : ((HandlerMethodImpl<? extends Throwable>) handler).getInjectionPoints()) {
                    try {
                        bm.validate(ip);
                    } catch (InjectionException e) {
                        adv.addDeploymentProblem(e);
                    }
                }
            }
        }
    }

    /**
     * Obtains the applicable handlers for the given type or super type of the given type.  Also makes use of {@link
     * org.jboss.solder.exception.control.ExceptionHandlerComparator} to order the handlers.
     *
     * @param exceptionClass    Type of exception to narrow handler list
     * @param bm                active BeanManager
     * @param handlerQualifiers additional handlerQualifiers to limit handlers
     * @param traversalMode     traversal limiter
     * @return An order collection of handlers for the given type.
     */
    public Collection<HandlerMethod<? extends Throwable>> getHandlersForExceptionType(Type exceptionClass, BeanManager bm,
                                                                                      Set<Annotation> handlerQualifiers,
                                                                                      TraversalMode traversalMode) {
        final Collection<HandlerMethod<? extends Throwable>> returningHandlers = new TreeSet<HandlerMethod<? extends Throwable>>(new ExceptionHandlerComparator());
        final HierarchyDiscovery h = new HierarchyDiscovery(exceptionClass);
        final Set<Type> closure = h.getTypeClosure();

        for (Type hierarchyType : closure) {
            if (this.allHandlers.get(hierarchyType) != null) {
                for (HandlerMethod<?> handler : this.allHandlers.get(hierarchyType)) {
                    if (handler.getTraversalMode() == traversalMode) {
                        if (handler.getQualifiers().contains(AnyLiteral.INSTANCE)) {
                            returningHandlers.add(handler);
                        } else {
                            if (!handlerQualifiers.isEmpty() && this.containsAny(handler.getQualifiers(), handlerQualifiers)) {
                                returningHandlers.add(handler);
                            }
                        }
                    }
                }
            }
        }

        log.foundHandlers(returningHandlers, exceptionClass, handlerQualifiers, traversalMode);
        return Collections.unmodifiableCollection(returningHandlers);
    }

    private boolean containsAny(final Collection<? extends Annotation> haystack,
                                final Collection<? extends Annotation> needles) {
        for (Annotation a : needles) {
            if (haystack.contains(a)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public <T extends Throwable> void registerHandlerMethod(HandlerMethod<T> handlerMethod) {
        log.addingHandler(handlerMethod);
        if (this.allHandlers.containsKey(handlerMethod.getExceptionType())) {
            this.allHandlers.get(handlerMethod.getExceptionType()).add(handlerMethod);
        } else {
            this.allHandlers.put(handlerMethod.getExceptionType(), new HashSet<HandlerMethod<? extends Throwable>>(Arrays.asList(handlerMethod)));
        }
    }
}
