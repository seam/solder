package org.jboss.weld.extensions.bean;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

public abstract class ForwardingInjectionTarget<T> implements InjectionTarget<T>
{
   
   protected abstract InjectionTarget<T> delegate();

   public void inject(T instance, CreationalContext<T> ctx)
   {
      delegate().inject(instance, ctx);
   }

   public void postConstruct(T instance)
   {
      delegate().postConstruct(instance);
   }

   public void preDestroy(T instance)
   {
      delegate().preDestroy(instance);
   }

   public void dispose(T instance)
   {
      delegate().dispose(instance);
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return delegate().getInjectionPoints();
   }

   public T produce(CreationalContext<T> ctx)
   {
      return delegate().produce(ctx);
   }

}
