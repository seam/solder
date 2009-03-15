package org.jboss.webbeans.environment.se;

import javax.context.ApplicationScoped;
import javax.event.Observes;
import javax.inject.manager.Manager;

import org.apache.log4j.Logger;
import org.jboss.webbeans.bootstrap.api.Bootstrap;
import org.jboss.webbeans.context.DependentContext;
import org.jboss.webbeans.environment.se.events.Shutdown;

@ApplicationScoped
public class ShutdownManager
{
   
   private static Logger log = Logger.getLogger(ShutdownManager.class);
   
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
            DependentContext.INSTANCE.setActive(false);
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
