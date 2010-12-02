package org.jboss.seam.solder.test.log;

import javax.inject.Inject;

import org.jboss.seam.solder.log.MessageBundle;

public class Jay
{
   
   @Inject @MessageBundle
   BirdMessages messages;
   
   String getMessage()
   {
      return messages.numberOfJaysSpotted(8);
   }

}
