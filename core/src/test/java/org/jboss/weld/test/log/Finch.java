package org.jboss.weld.test.log;

import javax.inject.Inject;

import org.jboss.weld.log.Category;
import org.slf4j.Logger;

public class Finch
{
   @Inject @Category("Finch")
   private Logger log;
   
   public void generateLogMessage()
   {
      log.info("A test message");
   }
}
