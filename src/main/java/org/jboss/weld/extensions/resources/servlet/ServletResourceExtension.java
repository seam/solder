package org.jboss.weld.extensions.resources.servlet;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.servlet.ServletContext;

/**
 * Portable extension that listens for the creation of ServletContexts and
 * stored them for other extensions to Consume
 * 
 * @author stuart
 * 
 */
public class ServletResourceExtension implements Extension
{
   protected Set<ServletContext> servletContexts = new HashSet<ServletContext>();

   public void registerServletContext(@Observes ServletContextInitializedEvent event)
   {
      servletContexts.add(event.getServletContext());
   }

   public void removeServletContext(@Observes ServletContextDestroyedEvent event)
   {
      servletContexts.remove(event.getServletContext());
   }

   public Set<ServletContext> getServletContexts()
   {
      return Collections.unmodifiableSet(servletContexts);
   }

}
