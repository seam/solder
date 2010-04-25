package org.jboss.weld.test.extensions.interceptor;

import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.testharness.impl.packaging.Classes;
import org.jboss.weld.extensions.interceptor.Interceptors;
import org.jboss.weld.test.AbstractWeldTest;
import org.testng.annotations.Test;

@Artifact
@Classes(Interceptors.class)
public class InterceptorsTest extends AbstractWeldTest
{
   
   @Test
   public void testInterceptorResolvable()
   {
      assert getReference(Interceptors.class).isInterceptorEnabled(FooInterceptor.class);
      // Waiting on WELD-503
    //  assert !getReference(Interceptors.class).isInterceptorEnabled(BarInterceptor.class);
      
   }

}
