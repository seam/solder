package org.jboss.weld.extensions.util.collections;

import java.util.ListIterator;

/** ListIterator decorator. */
public class WrappedListIterator<K, V> extends WrappedIterator<K, V> implements ListIterator<V>
{
   private final AbstractMultimap<K, V>.WrappedList list;

   WrappedListIterator(AbstractMultimap<K, V>.WrappedList collection)
   {
      super(collection);
      this.list = collection;
   }

   public WrappedListIterator(int index, AbstractMultimap<K, V>.WrappedList collection)
   {
      super(collection.getListDelegate().listIterator(index), collection);
      this.list = collection;
   }

   private ListIterator<V> getDelegateListIterator()
   {
      return (ListIterator<V>) getDelegateIterator();
   }

   public boolean hasPrevious()
   {
      return getDelegateListIterator().hasPrevious();
   }

   public V previous()
   {
      return getDelegateListIterator().previous();
   }

   public int nextIndex()
   {
      return getDelegateListIterator().nextIndex();
   }

   public int previousIndex()
   {
      return getDelegateListIterator().previousIndex();
   }

   public void set(V value)
   {
      getDelegateListIterator().set(value);
   }

   public void add(V value)
   {
      boolean wasEmpty = list.isEmpty();
      getDelegateListIterator().add(value);
      list.getParent().totalSize++;
      if (wasEmpty)
      {
         list.addToMap();
      }
   }
}