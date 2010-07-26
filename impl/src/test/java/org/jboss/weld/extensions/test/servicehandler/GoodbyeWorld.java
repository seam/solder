package org.jboss.weld.extensions.test.servicehandler;

@EchoService
public abstract class GoodbyeWorld
{
   public String otherMethod()
   {
      return "not saying goodbye";
   }

   public abstract String goodbyeWorld();

}
