package org.jboss.weld.test.extensions.interceptor;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Interceptor
@PrimaryInterceptionBinding
public class FooInterceptor
{

   @AroundInvoke
   public void aroundInvoke(InvocationContext ctx) throws Exception 
   {
      
   }
   
}
