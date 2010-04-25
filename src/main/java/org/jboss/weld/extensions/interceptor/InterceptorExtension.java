package org.jboss.weld.extensions.interceptor;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.Interceptor;
import javax.enterprise.inject.spi.ProcessBean;

public class InterceptorExtension implements Extension
{
   
   private final Collection<Class<?>> enabledInterceptors;
   
   public InterceptorExtension()
   {
      this.enabledInterceptors = Collections.synchronizedSet(new HashSet<Class<?>>());
   }

   @SuppressWarnings("unused")
   public void observeInterceptors(@Observes ProcessBean<?> pmb)
   {
      if (pmb.getBean() instanceof Interceptor<?>)
      {
         this.enabledInterceptors.add(pmb.getBean().getBeanClass());
      }
   }
   
   public Collection<Class<?>> getEnabledInterceptors()
   {
      return enabledInterceptors;
   }
   
}
