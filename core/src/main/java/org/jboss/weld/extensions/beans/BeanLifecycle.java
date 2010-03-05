package org.jboss.weld.extensions.beans;

import javax.enterprise.context.spi.CreationalContext;

public interface BeanLifecycle<T>
{
   public T create(CustomBean<T> bean, CreationalContext<T> arg0);

   public void destroy(CustomBean<T> bean, T arg0, CreationalContext<T> arg1);

}
