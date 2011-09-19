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

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;


/**
 * Collection iterator for {@code WrappedCollection}.
 */
public class WrappedIterator<K, V> implements Iterator<V> {
    final Iterator<V> delegateIterator;
    final Collection<V> originalDelegate;
    private final WrappedCollection<K, V> collection;

    WrappedIterator(WrappedCollection<K, V> collection) {
        this.collection = collection;
        delegateIterator = collection.getParent().iteratorOrListIterator(collection.delegate);
        originalDelegate = collection.delegate;

    }

    WrappedIterator(Iterator<V> delegateIterator, WrappedCollection<K, V> collection) {
        this.delegateIterator = delegateIterator;
        this.collection = collection;
        originalDelegate = collection.delegate;
    }

    /**
     * If the delegate changed since the iterator was created, the iterator is no
     * longer valid.
     */
    void validateIterator() {
        collection.refreshIfEmpty();
        if (collection.delegate != originalDelegate) {
            throw new ConcurrentModificationException();
        }
    }

    public boolean hasNext() {
        validateIterator();
        return delegateIterator.hasNext();
    }

    public V next() {
        validateIterator();
        return delegateIterator.next();
    }

    public void remove() {
        delegateIterator.remove();
        collection.getParent().totalSize--;
        collection.removeIfEmpty();
    }

    Iterator<V> getDelegateIterator() {
        validateIterator();
        return delegateIterator;
    }
}
