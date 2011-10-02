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

/**
 * Controller for stack trace filtering.
 *
 * @author <a href="http://community.jboss.org/people/LightGuard">Jason Porter</a>
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public interface StackFrame {
    /**
     * @return {@link StackTraceElement} represented by this.
     */
    StackTraceElement getStackTraceElement();

    /**
     * Sets a mark on a particular stack element. Typically used to "fold" stack elements.
     *
     * @param tag unique marker key.
     */
    void mark(String tag);

    /**
     * Obtains the stack element that was marked with the given tag.
     *
     * @param tag marker key to find
     * @return stack element that was first marked with the tag
     */
    StackFrame getMarkedFrame(String tag);

    /**
     * Checks if the given marker key has been set.
     *
     * @param tag marker key
     * @return true if the marker for the given key has been set
     */
    boolean isMarkSet(String tag);

    /**
     * Removes the given marker.
     *
     * @param tag marker key
     */
    void clearMark(String tag);

    /**
     * Change the {@link StackTraceElement} for this frame.
     *
     * @param element new element
     */
    void setStackTraceElement(StackTraceElement element);

    /**
     * Retrieves the index of this frame in the stack trace.
     *
     * @return index of this frame
     */
    int getIndex();
}
