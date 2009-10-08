package org.jboss.weld.test.log;

import javax.inject.Inject;

import org.jboss.weld.log.Log;
import org.jboss.weld.log.Logger;

class Sparrow
{
   @Inject @Logger
   private Log log;
   
   public void generateLogMessage()
   {
      log.info("A test message");
   }
}
