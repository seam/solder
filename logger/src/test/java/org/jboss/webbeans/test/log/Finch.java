package org.jboss.webbeans.test.log;

import javax.inject.Inject;

import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logger;

public class Finch
{
   @Inject @Logger("Finch")
   private Log log;
   
   public void generateLogMessage()
   {
      log.info("A test message");
   }
}
