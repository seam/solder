package org.jboss.weld.extensions.test.autoproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.inject.Inject;

public class HelloServiceHandler implements InvocationHandler
{

   @Inject
   HelloGenerator generator;

   public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
   {
      return generator.getHelloString() + method.getAnnotation(PersonName.class).value();
   }

}
