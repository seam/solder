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

/**
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 */
package org.jboss.solder.exception.control;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Container for the exception and it's stack trace.
 */
public final class ExceptionStackItem implements Serializable {
    private static final long serialVersionUID = 3082750572813491654L;

    final private Throwable throwable;
    final private StackTraceElement[] stackTraceElements;

    public ExceptionStackItem(final Throwable cause) {
        this(cause, cause.getStackTrace());
    }

    public ExceptionStackItem(Throwable throwable, StackTraceElement[] stackTraceElements) {
        this.stackTraceElements = stackTraceElements.clone();
        this.throwable = throwable;
    }

    public StackTraceElement[] getStackTraceElements() {
        return this.stackTraceElements.clone();
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    @Override
    public String toString() {
        return new StringBuilder().
                append("throwable: ").append(throwable).append(", ").
                append("stackTraceElements: ").append(stackTraceElements).
                toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExceptionStackItem that = (ExceptionStackItem) o;

        if (!Arrays.equals(stackTraceElements, that.stackTraceElements)) {
            return false;
        }
        if (!throwable.equals(that.throwable)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = throwable.hashCode();
        result = 31 * result + Arrays.hashCode(stackTraceElements);
        return result;
    }
}
