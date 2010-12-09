package org.jboss.seam.solder.util.collections;

public class Objects
{

   private Objects()
   {
   }

   /**
    * Determines whether two possibly-null objects are equal. Returns:
    * 
    * <ul>
    * <li>{@code true} if {@code a} and {@code b} are both null.
    * <li>{@code true} if {@code a} and {@code b} are both non-null and they are
    * equal according to {@link Object#equals(Object)}.
    * <li>{@code false} in all other situations.
    * </ul>
    * 
    * <p>
    * This assumes that any non-null objects passed to this function conform to
    * the {@code equals()} contract.
    */
   public static boolean equal(Object a, Object b)
   {
      return a == b || (a != null && a.equals(b));
   }

}
