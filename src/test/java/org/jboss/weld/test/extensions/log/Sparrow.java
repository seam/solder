package org.jboss.weld.test.extensions.log;

import javax.inject.Inject;

import org.slf4j.Logger;

class Sparrow
{
   @Inject
   private Logger log;
   
   public void generateLogMessage()
   {
      log.info("Sparrow");
   }
}
