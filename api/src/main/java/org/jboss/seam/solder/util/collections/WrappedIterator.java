package org.jboss.seam.solder.util.collections;

import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;


/** Collection iterator for {@code WrappedCollection}. */
public class WrappedIterator<K, V> implements Iterator<V>
{
   final Iterator<V> delegateIterator;
   final Collection<V> originalDelegate;
   private final WrappedCollection<K, V> collection;

   WrappedIterator(WrappedCollection<K, V> collection)
   {
      this.collection = collection;
      delegateIterator = collection.getParent().iteratorOrListIterator(collection.delegate);
      originalDelegate = collection.delegate;
      
   }

   WrappedIterator(Iterator<V> delegateIterator, WrappedCollection<K, V> collection)
   {
      this.delegateIterator = delegateIterator;
      this.collection = collection;
      originalDelegate = collection.delegate;
   }

   /**
    * If the delegate changed since the iterator was created, the iterator is no
    * longer valid.
    */
   void validateIterator()
   {
      collection.refreshIfEmpty();
      if (collection.delegate != originalDelegate)
      {
         throw new ConcurrentModificationException();
      }
   }

   public boolean hasNext()
   {
      validateIterator();
      return delegateIterator.hasNext();
   }

   public V next()
   {
      validateIterator();
      return delegateIterator.next();
   }

   public void remove()
   {
      delegateIterator.remove();
      collection.getParent().totalSize--;
      collection.removeIfEmpty();
   }

   Iterator<V> getDelegateIterator()
   {
      validateIterator();
      return delegateIterator;
   }
}
