package org.jboss.weld.extensions.interceptor;

import javax.inject.Inject;

public class Interceptors
{
   
   @Inject
   private InterceptorExtension interceptorExtension;
   
   private Interceptors()
   {
      // TODO Auto-generated constructor stub
   }
   
   public boolean isInterceptorEnabled(Class<?> clazz)
   {
      return interceptorExtension.getEnabledInterceptors().contains(clazz);
   }

}
