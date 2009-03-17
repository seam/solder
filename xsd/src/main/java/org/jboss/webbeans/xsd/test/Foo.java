package org.jboss.webbeans.xsd.test;

import javax.inject.Current;
import javax.inject.Initializer;

import org.jboss.webbeans.xsd.test.test.test.Tar;

@Current
public class Foo extends Bar
{
   @Current
   public String foo;
   public int poo;
   public Bar bar;
   public org.jboss.webbeans.xsd.test.test.Foo foo2;
   public Tar tar;

   @Initializer
   public Foo(String foo)
   {
   }

   @Current
   public String foo(@Current String foo)
   {
      return foo;
   }

   public void testy() {
      
   }
   
}
