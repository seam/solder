package org.jboss.webbeans.test.log;

import javax.inject.Inject;

import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logger;

class Sparrow
{
   @Inject @Logger
   private Log log;
   
   public void generateLogMessage()
   {
      log.info("A test message");
   }
}
