package org.jboss.weld.extensions.test.log;

import org.jboss.logging.Message;
import org.jboss.logging.MessageBundle;

@MessageBundle
public interface BirdMessages
{
   
   @Message("Spotted %s jays")
   String numberOfJaysSpotted(int number);

}
