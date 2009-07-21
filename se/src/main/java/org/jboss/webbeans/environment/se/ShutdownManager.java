package org.jboss.webbeans.environment.se;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Current;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.webbeans.bootstrap.api.Bootstrap;
import org.jboss.webbeans.environment.se.events.Shutdown;
import org.jboss.webbeans.log.LogProvider;
import org.jboss.webbeans.log.Logging;
import org.jboss.webbeans.manager.api.WebBeansManager;

@ApplicationScoped
public class ShutdownManager
{
   
   private static LogProvider log = Logging.getLogProvider(ShutdownManager.class);
   private @Current WebBeansManager manager;

   private boolean hasShutdownBeenCalled = false;
   
   private Bootstrap bootstrap;
   
   /**
    * The observer of the optional shutdown request which will in turn fire the
    * Shutdown event.
    * 
    * @param shutdownRequest
    */
   public void shutdown(@Observes @Shutdown BeanManager shutdownRequest)
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

   /**
    * Shutdown WebBeans SE gracefully (call this as an alternative to firing the
    * "@Shutdown Manager" event.
    */
   public void shutdown() {
       shutdown(manager);
   }
   
   public void setBootstrap(Bootstrap bootstrap)
   {
      this.bootstrap = bootstrap;
   }
   
}
