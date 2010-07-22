package org.jboss.weld.extensions.autoproxy;

import java.lang.reflect.Proxy;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * class that injects the proxy handler rather than a proxy
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 *
 * @param <T>
 */
public class DelegatingInjectionTarget<T> implements InjectionTarget<T>
{

   private final InjectionTarget delegate;

   public DelegatingInjectionTarget(InjectionTarget<T> delegate)
   {
      this.delegate = delegate;
   }

   public void inject(T instance, CreationalContext<T> ctx)
   {
      Object handler = Proxy.getInvocationHandler(instance);
      delegate.inject(handler, ctx);
   }

   public void postConstruct(T instance)
   {
      Object handler = Proxy.getInvocationHandler(instance);
      delegate.postConstruct(handler);
   }

   public void preDestroy(T instance)
   {
      Object handler = Proxy.getInvocationHandler(instance);
      delegate.preDestroy(handler);
   }

   public void dispose(T instance)
   {
      Object handler = Proxy.getInvocationHandler(instance);
      delegate.dispose(handler);
   }

   public Set<InjectionPoint> getInjectionPoints()
   {
      return delegate.getInjectionPoints();
   }

   public T produce(CreationalContext<T> ctx)
   {
      throw new RuntimeException("Not applicable");
   }

}
