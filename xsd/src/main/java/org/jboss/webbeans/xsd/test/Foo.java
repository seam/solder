package org.jboss.webbeans.xsd.test;

import javax.inject.Current;
import javax.inject.Initializer;

@Current
public class Foo
{
   @Current
   public String foo;

   @Initializer
   public Foo(String foo)
   {
   }

   @Current
   public String foo(@Current String foo)
   {
      return foo;
   }

}
