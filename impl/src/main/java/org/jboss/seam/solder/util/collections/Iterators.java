package org.jboss.seam.solder.util.collections;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Iterators
{

   private static final Iterator<Object> EMPTY_MODIFIABLE_ITERATOR = new Iterator<Object>()
   {
      /* @Override */public boolean hasNext()
      {
         return false;
      }

      /* @Override */public Object next()
      {
         throw new NoSuchElementException();
      }

      /* @Override */public void remove()
      {
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
   static <T> Iterator<T> emptyModifiableIterator()
   {
      return (Iterator<T>) EMPTY_MODIFIABLE_ITERATOR;
   }

}
