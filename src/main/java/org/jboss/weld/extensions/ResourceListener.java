package org.jboss.weld.extensions;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jboss.weld.extensions.resources.servlet.ServletContextDestroyedEvent;
import org.jboss.weld.extensions.resources.servlet.ServletContextInitializedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ServletContextListener that fires a CDI event when the ServletContext is
 * created or destroyed
 * 
 * @author Stuart Douglas
 * 
 */
//@WebListener
public class ResourceListener implements ServletContextListener
{
   private BeanManager beanManager;

   private Logger log = LoggerFactory.getLogger(ResourceListener.class);
   // FIXME: hack to work around invalid binding in JBoss AS 6 M2
   private static final List<String> beanManagerLocations = new ArrayList<String>()
   {
      private static final long serialVersionUID = 1L;
      {
         add("java:comp/BeanManager");
         add("java:app/BeanManager");
      }
   };

   public ResourceListener()
   {
      beanManager = lookupBeanManager();
   }

   private BeanManager lookupBeanManager()
   {
      for (String location : beanManagerLocations)
      {
         try
         {
            log.trace("Looking for Bean Manager at JNDI location #0", location);
            return (BeanManager) new InitialContext().lookup(location);
         }
         catch (NamingException e)
         {
            // No panic, keep trying
            log.debug("Bean Manager not found at JNDI location #0", location);
         }
      }
      // OK, panic
      throw new IllegalArgumentException("Could not find BeanManager in " + beanManagerLocations);
   }

   private void fireEvent(Object payload, Annotation... qualifiers)
   {
      log.trace("Firing event #0 with qualifiers #1", payload, qualifiers);
      beanManager.fireEvent(payload, qualifiers);
   }

   public void contextDestroyed(ServletContextEvent sce)
   {
      fireEvent(new ServletContextDestroyedEvent(sce.getServletContext()));
   }

   public void contextInitialized(ServletContextEvent sce)
   {
      fireEvent(new ServletContextInitializedEvent(sce.getServletContext()));
   }

}
