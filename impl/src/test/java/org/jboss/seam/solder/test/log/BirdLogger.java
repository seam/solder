package org.jboss.seam.solder.test.log;

import org.jboss.logging.LogMessage;
import org.jboss.logging.Message;
import org.jboss.logging.MessageLogger;

@MessageLogger
public interface BirdLogger extends BirdMessages
{
   
   @LogMessage @Message("Spotted %s Hawks") 
   void logHawksSpotted(int number);

}
