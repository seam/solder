package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * 
 * @author Stuart Douglas
 * 
 */
class TypeStore
{

   final Set<Type> types = new HashSet<Type>();

   public void add(Class<?> beanType)
   {
      Class<?> c = beanType;
      do
      {
         types.add(c);
         c = c.getSuperclass();
      }
      while (c != null);
      for (Class<?> i : beanType.getInterfaces())
      {
         types.add(i);
      }
   }

   public void addInterfaces(Class<?> beanType)
   {
      for (Class<?> i : beanType.getInterfaces())
      {
         types.add(i);
      }
   }

   public Set<Type> getTypes()
   {
      return types;
   }

}
