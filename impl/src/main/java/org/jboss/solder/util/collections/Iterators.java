/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.solder.util.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iterators {

    private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>() {
        /* @Override */
        public boolean hasNext() {
            return false;
        }

        /* @Override */
        public Object next() {
            throw new NoSuchElementException();
        }

        /* @Override */
        public void remove() {
            throw new IllegalStateException();
        }
    };

    /**
     * Returns the empty {@code Iterator} that throws
     * {@link IllegalStateException} instead of
     * {@link UnsupportedOperationException} on a call to
     * {@link Iterator#remove()}.
     */
    // Casting to any type is safe since there are no actual elements.
    @SuppressWarnings("unchecked")
    static <T> Iterator<T> emptyModifiableIterator() {
        return (Iterator<T>) EMPTY_MODIFIABLE_ITERATOR;
    }

}
