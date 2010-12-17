package org.jboss.seam.solder.util.collections;

import java.util.Set;

public class Collections2
{
   
   private Collections2() {}
   
   static boolean setEquals(Set<?> thisSet, Object object) {
      if (object == thisSet) {
        return true;
      }
      if (object instanceof Set<?>) {
        Set<?> thatSet = (Set<?>) object;
        return thisSet.size() == thatSet.size()
            && thisSet.containsAll(thatSet);
      }
      return false;
    }

}
