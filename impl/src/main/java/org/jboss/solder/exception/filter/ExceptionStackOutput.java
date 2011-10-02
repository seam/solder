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
package org.jboss.solder.exception.filter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.List;

import org.jboss.solder.exception.control.ExceptionStack;
import org.jboss.solder.exception.control.ExceptionStackItem;
import org.jboss.solder.exception.filter.StackFrame;
import org.jboss.solder.exception.filter.StackFrameFilter;

/**
 * This replaces the typical output of originalException stack traces. The stack is printed inverted of the standard
 * way, meaning the stack is unwrapped and the root cause is printed first followed by the next exception that wrapped
 * the root cause. This class is immutable.
 * <p/>
 * It may also make use of {@link StackFrameFilter} instances to filter the stack trace output.
 */
public class ExceptionStackOutput<T extends Throwable> {
    private final Deque<ExceptionStackItem> exceptionStackItems;
    private final StackFrameFilter<T> filter;

    // TODO: Really needs to be a properties file or something
    public static final String ROOT_CAUSE_TEXT = new StringBuilder("Root exception {0}").append(System.getProperty("line.separator")).toString();
    public static final String AT_TEXT = new StringBuilder("\t at {0}").append(System.getProperty("line.separator")).toString();
    public static final String WRAPPED_BY_TEXT = new StringBuilder("Wrapped within {0} and re-thrown").append(System.getProperty("line.separator")).toString();

    /**
     * Constructor to be used if not filtering is desired.
     *
     * @param exception Exception containing stack to be displayed
     */
    public ExceptionStackOutput(final T exception) {
        this(exception, null);
    }

    /**
     * Constructor which includes filtering
     *
     * @param exception Exception containing stack to be displayed
     * @param filter    a {@link StackFrameFilter} instance used to do the filtering
     */
    public ExceptionStackOutput(final T exception, final StackFrameFilter<T> filter) {
        this.exceptionStackItems = new ExceptionStack(exception).getOrigExceptionStackItems();
        this.filter = filter;
    }

    /**
     * Prints the stack trace for this instance, using any current filters.
     *
     * @return stack trace in string representation
     */
    public String printTrace() {
        final StringBuilder traceBuffer = new StringBuilder();
        final int exceptionStackItemsSize = this.exceptionStackItems.size();

        for (int i = 0; i < exceptionStackItemsSize; i++) {
            final ExceptionStackItem item = this.exceptionStackItems.removeFirst();
            final ExceptionStackItem nextItem = this.exceptionStackItems.peekFirst();

            if (i == 0) {
                traceBuffer.append(MessageFormat.format(ROOT_CAUSE_TEXT, item.getThrowable()));
            } else {
                traceBuffer.append(MessageFormat.format(WRAPPED_BY_TEXT, item.getThrowable()));
            }

            Collection<StackFrame> stackFrames;

            if (nextItem != null) {
                stackFrames = this.buildCollectionUpToNextWrapper(item.getThrowable(), nextItem.getThrowable());
            } else {
                stackFrames = this.createStackFrameCollectionFrom(item.getThrowable());
            }
            trace_loop:
            for (StackFrame stackFrame : stackFrames) {
                if (this.filter != null) {
                    switch (this.filter.process(stackFrame)) {
                        case TERMINATE_AFTER:
                            traceBuffer.append(MessageFormat.format(AT_TEXT, stackFrame.getStackTraceElement()));
                        case TERMINATE:
                        case DROP_REMAINING:
                            break trace_loop;
                        case DROP:

                            continue;
                        default:
                            traceBuffer.append(MessageFormat.format(AT_TEXT, stackFrame.getStackTraceElement()));
                    }
                } else {
                    traceBuffer.append(MessageFormat.format(AT_TEXT, stackFrame.getStackTraceElement()));
                }
            }
        }

        return traceBuffer.toString();
    }

    private Collection<StackFrame> createStackFrameCollectionFrom(final Throwable throwable) {
        final List<StackFrame> frameList = new ArrayList<StackFrame>(throwable.getStackTrace().length);

        for (int i = 0; i < throwable.getStackTrace().length; i++) {
            if (i == 0) {
                frameList.add(new StackFrameImpl(throwable));
            } else {
                frameList.add(new StackFrameImpl((StackFrameImpl) frameList.get(i - 1), throwable.getStackTrace()[i], i));
            }
        }
        return frameList;
    }

    private Collection<StackFrame> buildCollectionUpToNextWrapper(final Throwable t1, final Throwable t2) {
        final List<StackFrame> returningCollection = new ArrayList<StackFrame>();
        final StackTraceElement[] t1StackTraceElements = t1.getStackTrace();
        final StackTraceElement[] t2StackTraceElements = t2.getStackTrace();
        StackFrame previousFrame;

        int i;
        for (i = 0; i < t1StackTraceElements.length; i++) {
            if (i == 0) {
                final StackFrameImpl newFrame = new StackFrameImpl(t1);
                previousFrame = newFrame;
                if (!returningCollection.contains(newFrame)) {
                    returningCollection.add(newFrame);
                }
            } else {
                final StackFrameImpl newFrame = new StackFrameImpl((StackFrameImpl) returningCollection.get(i - 1), t1StackTraceElements[i + 1], i);
                previousFrame = newFrame;
                if (!returningCollection.contains(newFrame)) {
                    returningCollection.add(newFrame);
                }
            }

            for (StackTraceElement t2StackTraceElement : t2StackTraceElements) {
                if (t1StackTraceElements[i].equals(t2StackTraceElement)) {
                    returningCollection.remove(previousFrame);
                    return returningCollection;
                }
            }

        }

        return returningCollection;
    }
}
