package org.jboss.webbeans.test.tomcat.examples;

import javax.enterprise.inject.Initializer;

public class Game
{
   private final int number;
   
   @Initializer
   Game(@Random int number)
   {
      this.number = number;
   }

   public int getNumber()
   {
      return number;
   }
   
}
