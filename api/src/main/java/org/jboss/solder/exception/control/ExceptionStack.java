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

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;

/**
 * Information about the current exception and exception cause container.  This object is not immutable.
 */
public class ExceptionStack implements Serializable {
    private static final long serialVersionUID = 5988683320170873619L;

    private boolean root;
    private boolean last;
    private int initialStackSize;
    private Throwable next;
    private Collection<ExceptionStackItem> remaining;
    private Deque<ExceptionStackItem> exceptionStackItems;
    private Deque<ExceptionStackItem> origExceptionStackItems;
    private Collection<Throwable> causes;
    private Throwable current;

    /**
     * Basic constructor, needed to make the class a bean, please don't use
     */
    public ExceptionStack() {
    } // needed to be a bean

    /**
     * Builds the stack from the given exception.
     *
     * @param exception Caught exception
     */
    public ExceptionStack(final Throwable exception) {
        if (exception == null) {
            throw new IllegalArgumentException("exception must not be null");
        }

        Throwable e = exception;
        this.exceptionStackItems = new ArrayDeque<ExceptionStackItem>();

        do {
            this.exceptionStackItems.addFirst(new ExceptionStackItem(e));
            if (e instanceof SQLException) {
                SQLException sqlException = (SQLException) e;

                while (sqlException.getNextException() != null) {
                    sqlException = sqlException.getNextException();
                    this.exceptionStackItems.addFirst(new ExceptionStackItem(sqlException));
                }
            }
        }
        while ((e = e.getCause()) != null);

        this.initialStackSize = this.exceptionStackItems.size();
        this.causes = this.createThrowableCollectionFrom(exceptionStackItems);
        this.origExceptionStackItems = new ArrayDeque<ExceptionStackItem>(exceptionStackItems);
        this.init();
    }

    /**
     * Basic constructor.
     *
     * @param causeChainElements  collection of all causing elements for an exception from top to bottom (not
     *                            unwrapped).
     * @param currentElementIndex index of current element within the causeChainElements.
     * @throws IllegalArgumentException if causeChainElements is empty or null.
     * @deprecated There shouldn't be a use for this, please use the other constructor
     */
    @Deprecated
    protected ExceptionStack(final Collection<Throwable> causeChainElements, final int currentElementIndex) {
        if (causeChainElements == null || causeChainElements.size() == 0) {
            throw new IllegalArgumentException("Null or empty collection of causeChainElements is not valid");
        }

        if (currentElementIndex >= causeChainElements.size()) {
            throw new IllegalArgumentException("currentElementIndex must be less than or equals to causeChainElements.size()");
        }
        this.exceptionStackItems = new ArrayDeque<ExceptionStackItem>(this.createExceptionStackCollectionFrom(causeChainElements));
        this.causes = Collections.unmodifiableCollection(causeChainElements);
        this.origExceptionStackItems = new ArrayDeque<ExceptionStackItem>(exceptionStackItems);
        this.init();
    }

    private void init() {
        this.root = this.exceptionStackItems.size() == this.initialStackSize;

        if (!this.exceptionStackItems.isEmpty()) {
            this.current = this.exceptionStackItems.removeFirst().getThrowable();
            this.remaining = Collections.unmodifiableCollection(this.exceptionStackItems);
        } else {
            this.remaining = Collections.emptyList();
            this.current = null;
        }

        this.last = this.remaining.isEmpty();
        this.next = (this.last) ? null : this.exceptionStackItems.peekFirst().getThrowable();
    }

    private Collection<ExceptionStackItem> createExceptionStackCollectionFrom(Collection<Throwable> throwables) {
        final Deque<ExceptionStackItem> returningCollection = new ArrayDeque<ExceptionStackItem>(throwables.size());

        for (Throwable t : throwables) {
            returningCollection.addFirst(new ExceptionStackItem(t));
        }

        return returningCollection;
    }

    private Collection<Throwable> createThrowableCollectionFrom(final Collection<ExceptionStackItem> exceptionStackItems) {
        final Deque<Throwable> returningCollection = new ArrayDeque<Throwable>(exceptionStackItems.size() + 1); // allow current

        for (ExceptionStackItem item : exceptionStackItems) {
            returningCollection.addFirst(item.getThrowable());
        }

        return returningCollection;
    }

    public Collection<Throwable> getCauseElements() {
        return this.causes == null ? Collections.<Throwable>emptyList() : Collections.unmodifiableCollection(this.causes);
    }

    /**
     * Test if iteration is finished
     *
     * @return finished with iteration
     */
    public boolean isLast() {
        return this.last;
    }

    public Throwable getNext() {
        return this.next;
    }

    public Collection<Throwable> getRemaining() {
        return this.remaining == null ? Collections.<Throwable>emptyList() : Collections.unmodifiableCollection(
            this.createThrowableCollectionFrom(this.remaining));
    }

    /**
     * Tests if the current exception is the root exception
     *
     * @return Returns true if iteration is at the root exception (top of the inverted stack)
     */
    public boolean isRoot() {
        return this.root;
    }

    /**
     * Current exception in the iteration
     *
     * @return current exception
     */
    public Throwable getCurrent() {
        return this.current;
    }

    public void setCauseElements(Collection<Throwable> elements) {
        this.exceptionStackItems = new ArrayDeque<ExceptionStackItem>(this.createExceptionStackCollectionFrom(elements));
        this.init();
    }

    /**
     * The original exception stack if it has been changed.
     *
     * @return The original exception stack
     */
    public Deque<ExceptionStackItem> getOrigExceptionStackItems() {
        return new ArrayDeque<ExceptionStackItem>(this.origExceptionStackItems);
    }

    protected void dropCause() {
        this.init();
    }
}
