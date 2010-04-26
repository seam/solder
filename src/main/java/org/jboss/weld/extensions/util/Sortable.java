package org.jboss.weld.extensions.util;


/**
 * A Sortable class is given a precedence which is used to decide it's relative order
 * 
 * @author pmuir
 *
 */
public interface Sortable
{
   
   /**
    * A comparator which can order Sortables
    * 
    * @author pmuir
    *
    */
   public class Comparator implements java.util.Comparator<Sortable>
   {
      public int compare(Sortable arg1, Sortable arg2)
      {
         return -1 * Integer.valueOf(arg1.getPrecedence()).compareTo(Integer.valueOf(arg2.getPrecedence()));
      }
   }

   /**
    * An integer precedence value that indicates how favorable the implementation
    * considers itself amongst alternatives. A higher value is a higher
    * precedence. If two implementations have the save precedence, the order is undetermined.
    */
   public abstract int getPrecedence();

}