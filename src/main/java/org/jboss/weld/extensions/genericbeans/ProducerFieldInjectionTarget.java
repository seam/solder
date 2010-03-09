package org.jboss.weld.extensions.genericbeans;

import java.util.List;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * injection target wrapper used for beans that have generic producer fields
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 * @param <T>
 */
public class ProducerFieldInjectionTarget<T> implements InjectionTarget<T>
{
   private final InjectionTarget<T> delegate;
   private final List<FieldSetter> fieldSetters;

   public ProducerFieldInjectionTarget(InjectionTarget<T> delegate, List<FieldSetter> fieldSetters)
   {
      this.delegate = delegate;
      this.fieldSetters = fieldSetters;
   }

   public void inject(T instance, CreationalContext<T> ctx)
   {
      for (FieldSetter f : fieldSetters)
      {
         f.set(instance, ctx);
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

}
