package org.jboss.webbeans.test.log;

import org.jboss.webbeans.annotation.Logger;
import org.jboss.webbeans.log.Log;

public class Finch
{
   @Logger("Finch")
   private Log log;
   
   public void generateLogMessage()
   {
      log.info("A test message");
   }
}
