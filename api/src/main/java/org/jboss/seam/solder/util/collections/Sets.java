package org.jboss.seam.solder.util.collections;

import java.util.HashSet;
import java.util.Set;

public class Sets
{

   private Sets()
   {
   }

   /**
    * Calculates and returns the hash code of {@code s}.
    */
   static int hashCodeImpl(Set<?> s)
   {
      int hashCode = 0;
      for (Object o : s)
      {
         hashCode += o != null ? o.hashCode() : 0;
      }
      return hashCode;
   }

   /**
    * Creates an empty {@code HashSet} instance with enough capacity to hold the
    * specified number of elements without rehashing.
    * 
    * @param expectedSize the expected size
    * @return a new, empty {@code HashSet} with enough capacity to hold {@code
    *         expectedSize} elements without rehashing
    * @throws IllegalArgumentException if {@code expectedSize} is negative
    */
   public static <E> HashSet<E> newHashSetWithExpectedSize(int expectedSize)
   {
      return new HashSet<E>(Maps.capacity(expectedSize));
   }

}
