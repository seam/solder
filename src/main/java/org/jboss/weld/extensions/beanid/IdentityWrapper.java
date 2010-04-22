package org.jboss.weld.extensions.beanid;

/**
 * wrapper that overrides equals and hashCode to work on object identity
 * 
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 *
 */
public class IdentityWrapper
{
   final Object object;

   public IdentityWrapper(Object object)
   {
      this.object = object;
   }

   public Object getObject()
   {
      return object;
   }

   @Override
   public boolean equals(Object arg0)
   {
      if (arg0 instanceof IdentityWrapper)
      {
         IdentityWrapper w = (IdentityWrapper) arg0;
         return w.getObject() == object;
      }
      return object == arg0;
   }

   @Override
   public int hashCode()
   {
      return System.identityHashCode(object);
   }
}
