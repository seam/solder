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

/**
 * Payload for an exception to be handled.  This object is not immutable as small pieces of the state may be set by the
 * handler.
 *
 * @param <T> Exception type this event represents
 */
@SuppressWarnings({"unchecked"})
public class CaughtException<T extends Throwable> {
    /**
     * Flow control enum.  Used in the dispatcher to determine how to markHandled.
     */
    protected enum ExceptionHandlingFlow {
        HANDLED,
        MARK_HANDLED,
        DROP_CAUSE,
        ABORT,
        RETHROW,
        THROW
    }

    private final ExceptionStack exceptionStack;
    private final T exception;
    private boolean unmute;
    private ExceptionHandlingFlow flow;
    private Throwable throwNewException;
    private final boolean breadthFirstTraversal;
    private final boolean depthFirstTraversal;
    private final boolean markedHandled;

    /**
     * Initial state constructor.
     *
     * @param exceptionStack        Information about the current exception and cause chain.
     * @param breadthFirstTraversal flag indicating the direction of the cause chain traversal
     * @param handled               flag indicating the exception has already been handled by a previous handler
     * @throws IllegalArgumentException if exceptionStack is null
     */
    public CaughtException(final ExceptionStack exceptionStack, final boolean breadthFirstTraversal, final boolean handled) {
        if (exceptionStack == null) {
            throw new IllegalArgumentException("null is not valid for exceptionStack");
        }

        this.exception = (T) exceptionStack.getCurrent();
        this.exceptionStack = exceptionStack;
        this.breadthFirstTraversal = breadthFirstTraversal;
        this.depthFirstTraversal = !breadthFirstTraversal;
        this.markedHandled = handled;
        this.flow = ExceptionHandlingFlow.MARK_HANDLED;
    }

    public T getException() {
        return this.exception;
    }

    /**
     * Instructs the dispatcher to abort further processing of handlers.
     */
    public void abort() {
        this.flow = ExceptionHandlingFlow.ABORT;
    }

    /**
     * Instructs the dispatcher to rethrow the event exception after handler processing.
     */
    public void rethrow() {
        this.flow = ExceptionHandlingFlow.RETHROW;
    }

    /**
     * Instructs the dispatcher to terminate additional handler processing and mark the event as handled.
     */
    public void handled() {
        this.flow = ExceptionHandlingFlow.HANDLED;
    }

    /**
     * Default instruction to dispatcher, continues handler processing.
     */
    public void markHandled() {
        this.flow = ExceptionHandlingFlow.MARK_HANDLED;
    }

    /**
     * Similar to {@link CaughtException#markHandled()}, but instructs the dispatcher to markHandled to the next element
     * in the cause chain without processing additional handlers for this cause chain element.
     */
    public void dropCause() {
        this.flow = ExceptionHandlingFlow.DROP_CAUSE;
    }

    /**
     * Instructs the dispatcher to allow this handler to be invoked again.
     */
    public void unmute() {
        this.unmute = true;
    }

    public boolean isBreadthFirstTraversal() {
        return breadthFirstTraversal;
    }

    public boolean isDepthFirstTraversal() {
        return depthFirstTraversal;
    }

    protected boolean isUnmute() {
        return this.unmute;
    }

    public ExceptionStack getExceptionStack() {
        return this.exceptionStack;
    }

    protected ExceptionHandlingFlow getFlow() {
        return this.flow;
    }

    public boolean isMarkedHandled() {
        return this.markedHandled;
    }

    /**
     * Rethrow the exception, but use the given exception instead of the original.
     *
     * @param t Exception to be thrown in place of the original.
     */
    public void rethrow(Throwable t) {
        this.throwNewException = t;
        this.flow = ExceptionHandlingFlow.THROW;
    }

    protected Throwable getThrowNewException() {
        return throwNewException;
    }
}
