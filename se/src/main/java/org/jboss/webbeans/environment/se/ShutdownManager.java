package org.jboss.webbeans.environment.se;

import javax.context.ApplicationScoped;
import javax.event.Observes;
import javax.inject.manager.Manager;

import org.jboss.webbeans.bootstrap.api.Bootstrap;
import org.jboss.webbeans.environment.se.events.Shutdown;
import org.jboss.webbeans.log.LogProvider;
import org.jboss.webbeans.log.Logging;

@ApplicationScoped
public class ShutdownManager
{
   
   private static LogProvider log = Logging.getLogProvider(ShutdownManager.class);
   
   private boolean hasShutdownBeenCalled = false;
   
   private Bootstrap bootstrap;
   
   /**
    * The observer of the optional shutdown request which will in turn fire the
    * Shutdown event.
    * 
    * @param shutdownRequest
    */
   public void shutdown(@Observes @Shutdown Manager shutdownRequest)
   {
      synchronized (this)
      {
         
         if (!hasShutdownBeenCalled)
         {
            hasShutdownBeenCalled = true; 
            bootstrap.shutdown();
         }
         else
         {
            log.debug("Skipping spurious call to shutdown");
            log.trace(Thread.currentThread().getStackTrace());
         }
      }
   }
   
   public void setBootstrap(Bootstrap bootstrap)
   {
      this.bootstrap = bootstrap;
   }
   
}
