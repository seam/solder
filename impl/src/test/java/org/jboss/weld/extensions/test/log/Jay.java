package org.jboss.weld.extensions.test.log;

import javax.inject.Inject;

import org.jboss.weld.extensions.log.MessageBundle;

public class Jay
{
   
   @Inject @MessageBundle
   BirdMessages messages;
   
   String getMessage()
   {
      return messages.numberOfJaysSpotted(8);
   }

}
