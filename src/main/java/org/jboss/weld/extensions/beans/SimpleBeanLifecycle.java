package org.jboss.weld.extensions.beans;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.BeanManager;

public class SimpleBeanLifecycle<T> implements BeanLifecycle<T>
{
   final Class<T> type;
   final BeanManager beanManager;

   public SimpleBeanLifecycle(Class<T> type, BeanManager beanManager)
   {
      this.type = type;
      this.beanManager = beanManager;
   }

   public T create(CustomBean<T> bean, CreationalContext<T> creationalContext)
   {
      T instance = bean.getInjectionTarget().produce(creationalContext);
      bean.getInjectionTarget().inject(instance, creationalContext);
      bean.getInjectionTarget().postConstruct(instance);
      return instance;
   }

   public void destroy(CustomBean<T> bean, T instance, CreationalContext<T> creationalContext)
   {
      try
      {
         bean.getInjectionTarget().preDestroy(instance);
         creationalContext.release();
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

}
