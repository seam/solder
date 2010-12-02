package org.jboss.seam.solder.util.collections;

import static org.jboss.seam.solder.util.collections.Preconditions.checkArgument;

public class Multisets
{

   private Multisets()
   {
   }
   
   /**
    * Implementation of the {@code equals}, {@code hashCode}, and
    * {@code toString} methods of {@link Multiset.Entry}.
    */
   abstract static class AbstractEntry<E> implements Multiset.Entry<E> {
     /**
      * Indicates whether an object equals this entry, following the behavior
      * specified in {@link Multiset.Entry#equals}.
      */
     @Override public boolean equals(Object object) {
       if (object instanceof Multiset.Entry<?>) {
         Multiset.Entry<?> that = (Multiset.Entry<?>) object;
         return this.getCount() == that.getCount()
             && Objects.equal(this.getElement(), that.getElement());
       }
       return false;
     }

     /**
      * Return this entry's hash code, following the behavior specified in
      * {@link Multiset.Entry#hashCode}.
      */
     @Override public int hashCode() {
       E e = getElement();
       return ((e == null) ? 0 : e.hashCode()) ^ getCount();
     }

     /**
      * Returns a string representation of this multiset entry. The string
      * representation consists of the associated element if the associated count
      * is one, and otherwise the associated element followed by the characters
      * " x " (space, x and space) followed by the count. Elements and counts are
      * converted to strings as by {@code String.valueOf}.
      */
     @Override public String toString() {
       String text = String.valueOf(getElement());
       int n = getCount();
       return (n == 1) ? text : (text + " x " + n);
     }
   }


   static <E> boolean setCountImpl(Multiset<E> self, E element, int oldCount, int newCount)
   {
      checkNonnegative(oldCount, "oldCount");
      checkNonnegative(newCount, "newCount");

      if (self.count(element) == oldCount)
      {
         self.setCount(element, newCount);
         return true;
      }
      else
      {
         return false;
      }
   }
   
   static <E> int setCountImpl(Multiset<E> self, E element, int count) {
      checkNonnegative(count, "count");

      int oldCount = self.count(element);

      int delta = count - oldCount;
      if (delta > 0) {
        self.add(element, delta);
      } else if (delta < 0) {
        self.remove(element, -delta);
      }

      return oldCount;
    }

   static void checkNonnegative(int count, String name)
   {
      checkArgument(count >= 0, "%s cannot be negative: %s", name, count);
   }
}
