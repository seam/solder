package org.jboss.webbeans.test.log;

import org.jboss.webbeans.annotation.Logger;
import org.jboss.webbeans.log.Log;

class Sparrow
{
   @Logger
   private Log log;
   
   public void generateLogMessage()
   {
      log.info("A test message");
   }
}
