/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.solder.test.interceptor;

import org.junit.Test;

//@Artifact
//@Classes(Interceptors.class)
public class InterceptorTest // extends AbstractWeldTest
{

   // Disabled, needs a snapshot of Weld to work
   // @Test(enabled=false)
   @Test
   public void testInterceptorResolvable()
   {
      // assert
      // getReference(Interceptors.class).isInterceptorEnabled(FooInterceptor.class);
      // Waiting on WELD-503
      // assert
      // !getReference(Interceptors.class).isInterceptorEnabled(BarInterceptor.class);
      //
   }

}
