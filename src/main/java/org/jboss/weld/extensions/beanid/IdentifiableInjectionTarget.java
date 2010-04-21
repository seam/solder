package org.jboss.weld.extensions.beanid;

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
/**
 * wrapper around InjectionTarget that sets a bean id
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 *
 * @param <T>
 */
public class IdentifiableInjectionTarget<T> implements InjectionTarget<T>
{
   InjectionTarget<T> delegate;
   
   long id;
   
   IdentifiableInjectionTarget(InjectionTarget<T> delegate, long id)
   {
      this.delegate = delegate;
      this.id=id;
   }
   
   
   public void inject(T instance, CreationalContext<T> ctx)
   {
      IdentifiableBean bean =(IdentifiableBean)instance;
      bean.setBeanId(id);
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
