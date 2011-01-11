package org.jboss.seam.solder.test.log;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@Alternative
public class NonBean
{
   @Inject
   private Logger log;
   
   void logMessage()
   {
      log.info("Log message from non-bean");
   }
}
