package org.jboss.weld.extensions.resources.servlet;

import javax.servlet.ServletContext;

/**
 * Event class that is fired from a ServletContextListener
 * 
 * @author Stuart Douglas
 * 
 */
public class ServletContextDestroyedEvent
{
   ServletContext servletContext;

   public ServletContextDestroyedEvent(ServletContext servletContext)
   {
      this.servletContext = servletContext;
   }

   public ServletContext getServletContext()
   {
      return servletContext;
   }

}
