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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.enterprise.context.ConversationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Named;

import org.jboss.solder.exception.control.extension.CatchExtension;
import org.jboss.solder.exception.control.log.ExceptionHandlerDispatcherLog;
import org.jboss.solder.logging.Logger;

/**
 * Observer of {@link ExceptionToCatch} events and handler dispatcher. All handlers are invoked from this class.  This
 * class is immutable.
 */
public class ExceptionHandlerDispatch implements java.io.Serializable {
    private ExceptionToCatch exceptionToCatch;
    private ExceptionStack exceptionStack;

    private final ExceptionHandlerDispatcherLog log = Logger.getMessageLogger(ExceptionHandlerDispatcherLog.class,
            ExceptionHandlerDispatcherLog.class.getPackage().getName());

    /**
     * Observes the event, finds the correct exception handler(s) and invokes them.
     *
     * @param eventException exception to be invoked
     * @param bm             active bean manager
     * @param extension      catch extension instance to obtain handlers
     * @param stackEvent     Event for modifying the exception stack
     * @throws Throwable If a handler requests the exception to be re-thrown.
     */
    @SuppressWarnings({"unchecked", "MethodWithMultipleLoops", "ThrowableResultOfMethodCallIgnored"})
    public void executeHandlers(@Observes @Any ExceptionToCatch eventException, final BeanManager bm,
                                CatchExtension extension, Event<ExceptionStack> stackEvent) throws Throwable {
        log.enteringExceptionHandlerDispatcher(eventException.getException());

        CreationalContext<Object> ctx = null;
        this.exceptionToCatch = eventException;

        Throwable throwException = null;

        try {
            ctx = bm.createCreationalContext(null);

            final Set<HandlerMethod<?>> processedHandlers = new HashSet<HandlerMethod<?>>();

            final ExceptionStack stack = new ExceptionStack(eventException.getException());

            stackEvent.fire(stack); // Allow for modifying the exception stack

            // TODO: Clean this up so there's only the while and one for loop
            inbound_cause:
            while (stack.getCurrent() != null) {
                this.exceptionStack = stack;

                final List<HandlerMethod<?>> breadthFirstHandlerMethods = new ArrayList<HandlerMethod<?>>(
                        extension.getHandlersForExceptionType(stack.getCurrent().getClass(),
                                bm, eventException.getQualifiers(), TraversalMode.BREADTH_FIRST));

                for (HandlerMethod<?> handler : breadthFirstHandlerMethods) {
                    if (!processedHandlers.contains(handler)) {
                        log.notifyingHandler(handler);

                        @SuppressWarnings("rawtypes")
                        final CaughtException breadthFirstEvent = new CaughtException(stack, true, eventException.isHandled());
                        handler.notify(breadthFirstEvent, bm);

                        log.returnFromHandler(handler, breadthFirstEvent.getFlow().name());

                        if (!breadthFirstEvent.isUnmute()) {
                            processedHandlers.add(handler);
                        }

                        switch (breadthFirstEvent.getFlow()) {
                            case HANDLED:
                                eventException.setHandled(true);
                                return;
                            case MARK_HANDLED:
                                eventException.setHandled(true);
                                break;
                            case ABORT:
                                return;
                            case DROP_CAUSE:
                                eventException.setHandled(true);
                                stack.dropCause();
                                continue inbound_cause;
                            case RETHROW:
                                throwException = eventException.getException();
                                break;
                            case THROW:
                                throwException = breadthFirstEvent.getThrowNewException();
                        }
                    }
                }

                final List<HandlerMethod<? extends Throwable>> depthFirstHandlerMethods = new ArrayList<HandlerMethod<? extends Throwable>>(
                        extension.getHandlersForExceptionType(stack.getCurrent().getClass(),
                                bm, eventException.getQualifiers(), TraversalMode.DEPTH_FIRST));

                // Reverse these so category handlers are last
                Collections.reverse(depthFirstHandlerMethods);

                for (HandlerMethod<?> handler : depthFirstHandlerMethods) {
                    if (!processedHandlers.contains(handler)) {
                        log.notifyingHandler(handler);

                        @SuppressWarnings("rawtypes")
                        final CaughtException depthFirstEvent = new CaughtException(stack, false, eventException.isHandled());
                        handler.notify(depthFirstEvent, bm);

                        log.returnFromHandler(handler, depthFirstEvent.getFlow().name());

                        if (!depthFirstEvent.isUnmute()) {
                            processedHandlers.add(handler);
                        }

                        switch (depthFirstEvent.getFlow()) {
                            case HANDLED:
                                eventException.setHandled(true);
                                return;
                            case MARK_HANDLED:
                                eventException.setHandled(true);
                                break;
                            case ABORT:
                                return;
                            case DROP_CAUSE:
                                eventException.setHandled(true);
                                stack.dropCause();
                                continue inbound_cause;
                            case RETHROW:
                                throwException = eventException.getException();
                                break;
                            case THROW:
                                throwException = depthFirstEvent.getThrowNewException();
                        }
                    }
                }
                this.exceptionStack.dropCause();
            }

            if (!eventException.isHandled() && throwException == null) {
                log.noHandlersFound(eventException.getException());
                throw eventException.getException();
            }

            if (throwException != null) {
                throw throwException;
            }
        } finally {
            if (ctx != null) {
                ctx.release();
            }
        }

        log.endingExceptionHandlerDispatcher(exceptionToCatch.getException());
    }

    @Produces
    @ConversationScoped
    @Named("handledException")
    public ExceptionStack getExceptionStack() {
        return this.exceptionStack == null ? new ExceptionStack() : this.exceptionStack;
    }

    @Produces
    @ConversationScoped
    @Named("caughtException")
    public ExceptionToCatch getExceptionToCatch() {
        return this.exceptionToCatch == null ? new ExceptionToCatch() : this.exceptionToCatch;
    }
}
