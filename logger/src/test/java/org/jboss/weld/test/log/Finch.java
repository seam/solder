package org.jboss.weld.test.log;

import javax.inject.Inject;

import org.jboss.weld.log.Log;
import org.jboss.weld.log.Logger;

public class Finch
{
   @Inject @Logger("Finch")
   private Log log;
   
   public void generateLogMessage()
   {
      log.info("A test message");
   }
}
