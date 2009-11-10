package org.jboss.weld.test.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Qualifier;

import org.jboss.weld.test.spi.Bootstrap;

public class TestCore
{
   private Bootstrap boostrap;
   private BeanManager manager;

   public void start() throws Exception
   {
      boostrap = new BootstrapResolver().resolve();
      manager = boostrap.start();
   }

   public void stop()
   {
      boostrap.stop();
   }

   public <T> T getInstanceByType(Class<T> type, Annotation... bindings)
   {
      return getInstanceByType(manager, type, bindings);
   }

   @SuppressWarnings("unchecked")
   private <T> T getInstanceByType(BeanManager manager, Class<T> type,
         Annotation... bindings)
   {
      final Bean<?> bean = manager.getBeans(type, bindings).iterator().next();
      CreationalContext<?> cc = manager.createCreationalContext(bean);
      return (T) manager.getReference(bean, type, cc);
   }

   public void injectFields(Object target) throws Exception
   {
      Field[] fields = target.getClass().getDeclaredFields();
      for (Field field : fields)
      {
         injectField(field, target);
      }
   }

   private void injectField(Field field, Object target) throws Exception
   {
      if (!hasBindTypeAnnotation(field.getAnnotations()))
      {
         return;
      }
      if (!field.isAccessible())
      {
         field.setAccessible(true);
      }
      Object injectable = getInstanceByType(field.getType(), field
            .getAnnotations());

      if (injectable != null)
      {
         field.set(target, injectable);
      }
   }

   public static boolean hasBindTypeAnnotation(Annotation[] annotations)
   {
      for (Annotation annotation : annotations)
      {
         if (annotation.annotationType().isAnnotationPresent(Qualifier.class))
         {
            return true;
         }
      }
      return false;
   }
}
