package org.jboss.seam.solder.util.collections;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;
import java.util.Map;

public class Serialization
{

   private Serialization()
   {
   }

   /**
    * Reads a count corresponding to a serialized map, multiset, or multimap. It
    * returns the size of a map serialized by
    * {@link #writeMap(Map, ObjectOutputStream)}, the number of distinct
    * elements in a multiset serialized by
    * {@link #writeMultiset(Multiset, ObjectOutputStream)}, or the number of
    * distinct keys in a multimap serialized by
    * {@link #writeMultimap(Multimap, ObjectOutputStream)}.
    * 
    * <p>
    * The returned count may be used to construct an empty collection of the
    * appropriate capacity before calling any of the {@code populate} methods.
    */
   public static int readCount(ObjectInputStream stream) throws IOException
   {
      return stream.readInt();
   }

   /**
    * Stores the contents of a multimap in an output stream, as part of
    * serialization. It does not support concurrent multimaps whose content may
    * change while the method is running. The {@link Multimap#asMap} view
    * determines the ordering in which data is written to the stream.
    * 
    * <p>
    * The serialized output consists of the number of distinct keys, and then
    * for each distinct key: the key, the number of values for that key, and the
    * key's values.
    */
   public static <K, V> void writeMultimap(Multimap<K, V> multimap, ObjectOutputStream stream) throws IOException
   {
      stream.writeInt(multimap.asMap().size());
      for (Map.Entry<K, Collection<V>> entry : multimap.asMap().entrySet())
      {
         stream.writeObject(entry.getKey());
         stream.writeInt(entry.getValue().size());
         for (V value : entry.getValue())
         {
            stream.writeObject(value);
         }
      }
   }
   
   /**
    * Populates a multimap by reading an input stream, as part of
    * deserialization. See {@link #writeMultimap} for the data format. The number
    * of distinct keys is determined by a prior call to {@link #readCount}.
    */
   public static <K, V> void populateMultimap(
       Multimap<K, V> multimap, ObjectInputStream stream, int distinctKeys)
       throws IOException, ClassNotFoundException {
     for (int i = 0; i < distinctKeys; i++) {
       @SuppressWarnings("unchecked") // reading data stored by writeMultimap
       K key = (K) stream.readObject();
       Collection<V> values = multimap.get(key);
       int valueCount = stream.readInt();
       for (int j = 0; j < valueCount; j++) {
         @SuppressWarnings("unchecked") // reading data stored by writeMultimap
         V value = (V) stream.readObject();
         values.add(value);
       }
     }
   }

}
