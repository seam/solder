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
package org.jboss.solder.exception.filter;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import org.jboss.solder.exception.filter.StackFrame;

/**
 * Internal implementation of {@link StackFrame}.
 */
final class StackFrameImpl implements StackFrame {
    private StackTraceElement stackTraceElement;
    private final Map<String, StackFrame> markMap;
    private final int index;

    StackFrameImpl(final Throwable throwable) {
        this.index = 0;
        this.stackTraceElement = throwable.getStackTrace()[this.index];
        this.markMap = new HashMap<String, StackFrame>();
    }

    public StackFrameImpl(final StackFrameImpl copy, final StackTraceElement nextStackTraceElement, final int traceIndex) {
        this.stackTraceElement = nextStackTraceElement;
        this.markMap = copy.markMap;
        this.index = traceIndex;
    }

    @Override
    public StackTraceElement getStackTraceElement() {
        return this.stackTraceElement;
    }

    @Override
    public void mark(String tag) {
        this.markMap.put(tag, this);
    }

    @Override
    public StackFrame getMarkedFrame(String tag) {
        return this.markMap.get(tag);
    }

    @Override
    public boolean isMarkSet(String tag) {
        return this.markMap.containsKey(tag);
    }

    @Override
    public void clearMark(String tag) {
        this.markMap.remove(tag);
    }

    @Override
    public void setStackTraceElement(StackTraceElement element) {
        this.stackTraceElement = element;
    }

    @Override
    public int getIndex() {
        return this.index;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StackFrameImpl that = (StackFrameImpl) o;

        if (index != that.index) {
            return false;
        }
        if (!markMap.equals(that.markMap)) {
            return false;
        }
        if (!stackTraceElement.equals(that.stackTraceElement)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = stackTraceElement.hashCode();
        result = 31 * result + markMap.hashCode();
        result = 31 * result + index;
        return result;
    }

    @Override
    public String toString() {
        return new StringBuilder(MessageFormat.format("Element Index: {0}", this.index)).append(", ")
                .append(MessageFormat.format("element: {0}", this.stackTraceElement)).append(", ")
                .append(MessageFormat.format("tags: {0}", this.markMap.keySet())).toString();
    }
}
