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

import java.util.HashMap;
import java.util.Map.Entry;

import static org.jboss.solder.util.collections.Preconditions.checkArgument;

public class Maps {

    private Maps() {
    }

    /**
     * Returns an immutable map entry with the specified key and value. The
     * {@link Entry#setValue} operation throws an
     * {@link UnsupportedOperationException}.
     * <p/>
     * <p/>
     * The returned entry is serializable.
     *
     * @param key   the key to be associated with the returned entry
     * @param value the value to be associated with the returned entry
     */
    public static <K, V> Entry<K, V> immutableEntry(final K key, final V value) {
        return new ImmutableEntry<K, V>(key, value);
    }

    /**
     * Returns an appropriate value for the "capacity" (in reality, "minimum
     * table size") parameter of a {@link HashMap} constructor, such that the
     * resulting table will be between 25% and 50% full when it contains
     * {@code expectedSize} entries.
     *
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     */
    static int capacity(int expectedSize) {
        checkArgument(expectedSize >= 0);
        return Math.max(expectedSize * 2, 16);
    }

    /**
     * Creates a {@code HashMap} instance with enough capacity to hold the
     * specified number of elements without rehashing.
     *
     * @param expectedSize the expected size
     * @return a new, empty {@code HashMap} with enough
     *         capacity to hold {@code expectedSize} elements without rehashing
     * @throws IllegalArgumentException if {@code expectedSize} is negative
     */
    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(
            int expectedSize) {
        /*
        * The HashMap is constructed with an initialCapacity that's greater than
        * expectedSize. The larger value is necessary because HashMap resizes
        * its internal array if the map size exceeds loadFactor * initialCapacity.
        */
        return new HashMap<K, V>(capacity(expectedSize));
    }

}
