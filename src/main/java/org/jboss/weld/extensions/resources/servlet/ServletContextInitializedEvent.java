package org.jboss.weld.extensions.resources.servlet;

import javax.servlet.ServletContext;

/**
 * Event class that is fired from a ServletContextListener
 * 
 * @author Stuart Douglas
 * 
 */
public class ServletContextInitializedEvent
{
   ServletContext servletContext;

   public ServletContextInitializedEvent(ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }

   public ServletContext getServletContext()
   {
      return servletContext;
   }

}
