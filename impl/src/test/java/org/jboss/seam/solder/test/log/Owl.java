package org.jboss.seam.solder.test.log;

import javax.inject.Inject;

public class Owl
{
   @Inject
   private BirdLogger logger;
   
   public void generateLogMessage()
   {
      logger.logOwlsSpotted(5);
   }
}
