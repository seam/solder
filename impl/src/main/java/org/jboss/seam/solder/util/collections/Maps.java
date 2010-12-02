package org.jboss.seam.solder.util.collections;

import static org.jboss.seam.solder.util.collections.Preconditions.checkArgument;

import java.util.HashMap;
import java.util.Map.Entry;

public class Maps
{

   private Maps()
   {
   }

   /**
    * Returns an immutable map entry with the specified key and value. The
    * {@link Entry#setValue} operation throws an
    * {@link UnsupportedOperationException}.
    * 
    * <p>
    * The returned entry is serializable.
    * 
    * @param key the key to be associated with the returned entry
    * @param value the value to be associated with the returned entry
    */
   public static <K, V> Entry<K, V> immutableEntry(final K key, final V value)
   {
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
    *     capacity to hold {@code expectedSize} elements without rehashing
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
