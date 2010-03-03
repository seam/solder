package org.jboss.weld.extensions.genericbeans;

import java.lang.reflect.Field;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Class that is responsible for setting the values of generic producer fields
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 */
public class FieldSetter
{
   final Field field;
   final SyntheticQualifier qualifier;
   final BeanManager beanManager;

   public FieldSetter(BeanManager beanManager, Field field, SyntheticQualifier qualifier)
   {
      this.field = field;
      this.qualifier = qualifier;
      this.beanManager = beanManager;
      field.setAccessible(true);
   }

   public void set(Object instance, CreationalContext<?> ctx)
   {
      Set<Bean<?>> beans = beanManager.getBeans(field.getType(), qualifier);
      if (beans.size() == 0)
      {
         throw new RuntimeException("Could not resolve bean for Generic Producer field " + field.getDeclaringClass() + "." + field.getName() + " Type: " + field.getType() + " Qualifiers:" + qualifier);
      }
      if (beans.size() > 1)
      {
         throw new RuntimeException("More than 1 bean resolved for Generic Producer field " + field.getDeclaringClass() + "." + field.getName());
      }
      Bean bean = beans.iterator().next();
      Object dep = beanManager.getReference(bean, field.getType(), ctx);
      try
      {
         field.set(instance, dep);
      }
      catch (IllegalArgumentException e)
      {
         throw new RuntimeException(e);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(e);
      }

   }

}
