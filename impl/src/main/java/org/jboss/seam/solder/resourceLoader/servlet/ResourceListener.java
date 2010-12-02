package org.jboss.seam.solder.resourceLoader.servlet;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.seam.solder.resourceLoader.DelegatingResourceLoader;

/**
 * Servlet context listener than creates and registers a new
 * ServletContextLoader for each context
 * 
 * @author Stuart Douglas
 * 
 */
public class ResourceListener implements ServletContextListener
{

   public void contextDestroyed(ServletContextEvent sce)
   {
   }

   public void contextInitialized(ServletContextEvent sce)
   {
      DelegatingResourceLoader.addResourceLoader(new ServletContextLoader(sce.getServletContext()));
   }

}
