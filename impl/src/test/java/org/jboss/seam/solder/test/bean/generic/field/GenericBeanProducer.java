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
package org.jboss.seam.solder.test.bean.generic.field;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;

/**
 * A producer of generic beans
 * @author pmuir
 *
 */
public class GenericBeanProducer
{
   @SuppressWarnings("unused")
   @Foo(1)
   @Produces
   @Message("hello1")
   private Burt burt1;

   @SuppressWarnings("unused")
   @Foo(2)
   @Produces
   @Message("hello2")
   private Burt burt2;
   
   @SuppressWarnings("unused")
   @Foo(3)
   @Produces
   @Message("hello3")
   @SessionScoped
   private Burt baz3;
   
   @SuppressWarnings("unused")
   @Foo(4)
   @Produces
   @Message("hello4")
   @SessionScoped
   private Burt baz4;
   
   @SuppressWarnings("unused")
   @Foo(1)
   @Produces
   @Service(1)
   private Waldo waldo1 = new Waldo("Pete");
   
   @SuppressWarnings("unused")
   @Foo(2)
   @Produces
   @Service(2)
   private Waldo waldo2 = new Waldo("Stuart");

}
