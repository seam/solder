package org.jboss.weld.extensions.genericbeans;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.weld.extensions.util.reannotated.ReannotatedField;
import org.jboss.weld.extensions.util.reannotated.ReannotatedType;

/**
 * injection target wrapper that injects the configuration for generic beans
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 * @param <T>
 */
public class GenericBeanInjectionTargetWrapper<T> implements InjectionTarget<T>
{
   InjectionTarget<T> delegate;
   Annotation annotation;
   ReannotatedType<T> annotatedType;

   public GenericBeanInjectionTargetWrapper(ReannotatedType<T> annotatedType, InjectionTarget<T> delegate, Annotation annotation)
   {
      this.annotation = annotation;
      this.delegate = delegate;
      this.annotatedType = annotatedType;
   }

   public void inject(T instance, CreationalContext<T> ctx)
   {
      for (Field f : getFields(instance.getClass()))
      {

         if (annotation.annotationType().isAssignableFrom(f.getType()))
         {
            ReannotatedField<? super T> reannotatedField = annotatedType.getField(f);
            if (reannotatedField.isAnnotationPresent(InjectConfiguration.class))
            {
               try
               {
                  f.setAccessible(true);
                  f.set(instance, annotation);
               }
               catch (IllegalAccessException e)
               {
                  throw new RuntimeException(e);
               }
            }
         }
      }

      delegate.inject(instance, ctx);
   }

   public void postConstruct(T instance)
   {
      delegate.postConstruct(instance);
   }

   public void preDestroy(T instance)
   {
      delegate.preDestroy(instance);
   }

   public void dispose(T instance)
   {
      delegate.dispose(instance);
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return delegate.getInjectionPoints();
   }

   public T produce(CreationalContext<T> ctx)
   {
      return delegate.produce(ctx);
   }

   public static Set<Field> getFields(Class clazz)
   {
      Set<Field> ret = new HashSet<Field>();
      return getFields(clazz, ret);
   }

   private static Set<Field> getFields(Class clazz, Set<Field> ret)
   {
      ret.addAll(Arrays.asList(clazz.getDeclaredFields()));
      Class n = clazz.getSuperclass();
      if (n != Object.class)
      {
         return getFields(n);
      }
      return ret;
   }

}
