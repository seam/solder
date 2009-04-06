package org.jboss.webbeans.test.log;

import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logger;

class Sparrow
{
   @Logger
   private Log log;
   
   public void generateLogMessage()
   {
      log.info("A test message");
   }
}
