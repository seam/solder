package org.jboss.seam.solder.bean;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * An implementation of {@link InjectionTarget} that forwards all calls to
 * {@link #delegate()}.
 * 
 * @author Pete Muir
 * 
 * @param <T> The class of the instance
 */
public abstract class ForwardingInjectionTarget<T> implements InjectionTarget<T>
{

   /**
    * All calls to this {@link InjectionTarget} instance are forwarded to the
    * delegate unless overridden.
    * 
    * @return the delegate {@link InjectionTarget}
    */
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
