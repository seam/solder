package org.jboss.seam.solder.util.collections;

import java.util.List;

public class Platform
{
   
   private Platform() {}

   static <T> List<T> subList(List<T> list, int fromIndex, int toIndex)
   {
      return list.subList(fromIndex, toIndex);
   }

}
