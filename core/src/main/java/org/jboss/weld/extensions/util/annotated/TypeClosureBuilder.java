package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

/**
 * A type closure builder
 * 
 * @author Stuart Douglas
 * 
 */
class TypeClosureBuilder
{

   final Set<Type> types = new HashSet<Type>();

   public TypeClosureBuilder add(Type type)
   {
      types.add(type);
      return this;
   }

   public TypeClosureBuilder add(Class<?> beanType)
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
      return this;
   }

   public TypeClosureBuilder addInterfaces(Class<?> beanType)
   {
      for (Class<?> i : beanType.getInterfaces())
      {
         types.add(i);
      }
      return this;
   }

   public Set<Type> getTypes()
   {
      return types;
   }

}
