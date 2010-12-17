package org.jboss.seam.solder.util.collections;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

/** List decorator that stays in sync with the multimap values for a key. */
class WrappedList<K, V> extends WrappedCollection<K, V> implements List<V>
{
   /**
    * 
    */
   private final AbstractMultimap<K, V> abstractMultimap;

   WrappedList(AbstractMultimap<K, V> abstractMultimap, K key, List<V> delegate, WrappedCollection<K, V> ancestor)
   {
      super(abstractMultimap, key, delegate, ancestor);
      this.abstractMultimap = abstractMultimap;
   }

   List<V> getListDelegate()
   {
      return (List<V>) getDelegate();
   }

   public boolean addAll(int index, Collection<? extends V> c)
   {
      if (c.isEmpty())
      {
         return false;
      }
      int oldSize = size(); // calls refreshIfEmpty
      boolean changed = getListDelegate().addAll(index, c);
      if (changed)
      {
         int newSize = getDelegate().size();
         abstractMultimap.totalSize += (newSize - oldSize);
         if (oldSize == 0)
         {
            addToMap();
         }
      }
      return changed;
   }

   public V get(int index)
   {
      refreshIfEmpty();
      return getListDelegate().get(index);
   }

   public V set(int index, V element)
   {
      refreshIfEmpty();
      return getListDelegate().set(index, element);
   }

   public void add(int index, V element)
   {
      refreshIfEmpty();
      boolean wasEmpty = getDelegate().isEmpty();
      getListDelegate().add(index, element);
      abstractMultimap.totalSize++;
      if (wasEmpty)
      {
         addToMap();
      }
   }

   public V remove(int index)
   {
      refreshIfEmpty();
      V value = getListDelegate().remove(index);
      abstractMultimap.totalSize--;
      removeIfEmpty();
      return value;
   }

   public int indexOf(Object o)
   {
      refreshIfEmpty();
      return getListDelegate().indexOf(o);
   }

   public int lastIndexOf(Object o)
   {
      refreshIfEmpty();
      return getListDelegate().lastIndexOf(o);
   }

   public ListIterator<V> listIterator()
   {
      refreshIfEmpty();
      return new WrappedListIterator<K, V>(this);
   }

   public ListIterator<V> listIterator(int index)
   {
      refreshIfEmpty();
      return new WrappedListIterator<K, V>(index, this);
   }

   public List<V> subList(int fromIndex, int toIndex)
   {
      refreshIfEmpty();
      return abstractMultimap.wrapList(getKey(), Platform.subList(getListDelegate(), fromIndex, toIndex), (getAncestor() == null) ? this : getAncestor());
   }


}