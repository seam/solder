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

import java.util.ListIterator;

/**
 * ListIterator decorator.
 */
public class WrappedListIterator<K, V> extends WrappedIterator<K, V> implements ListIterator<V> {
    private final WrappedList<K, V> list;

    WrappedListIterator(WrappedList<K, V> collection) {
        super(collection);
        this.list = collection;
    }

    public WrappedListIterator(int index, WrappedList<K, V> collection) {
        super(collection.getListDelegate().listIterator(index), collection);
        this.list = collection;
    }

    private ListIterator<V> getDelegateListIterator() {
        return (ListIterator<V>) getDelegateIterator();
    }

    public boolean hasPrevious() {
        return getDelegateListIterator().hasPrevious();
    }

    public V previous() {
        return getDelegateListIterator().previous();
    }

    public int nextIndex() {
        return getDelegateListIterator().nextIndex();
    }

    public int previousIndex() {
        return getDelegateListIterator().previousIndex();
    }

    public void set(V value) {
        getDelegateListIterator().set(value);
    }

    public void add(V value) {
        boolean wasEmpty = list.isEmpty();
        getDelegateListIterator().add(value);
        list.getParent().totalSize++;
        if (wasEmpty) {
            list.addToMap();
        }
    }
}
