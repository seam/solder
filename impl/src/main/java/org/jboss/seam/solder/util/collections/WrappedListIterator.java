package org.jboss.seam.solder.util.collections;

import java.util.ListIterator;

/** ListIterator decorator. */
public class WrappedListIterator<K, V> extends WrappedIterator<K, V> implements ListIterator<V>
{
   private final WrappedList<K, V> list;

   WrappedListIterator(WrappedList<K, V> collection)
   {
      super(collection);
      this.list = collection;
   }

   public WrappedListIterator(int index, WrappedList<K, V> collection)
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