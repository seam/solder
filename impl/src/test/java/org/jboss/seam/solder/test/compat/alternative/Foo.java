package org.jboss.seam.solder.test.compat.alternative;

import javax.inject.Inject;

public class Foo
{
   @Inject
   private Bar bar;

   public Bar getBar()
   {
      return bar;
   }
}
