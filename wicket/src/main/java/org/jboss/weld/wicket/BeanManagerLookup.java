package org.jboss.weld.wicket;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.servlet.ServletContext;

import org.apache.wicket.Application;
import org.apache.wicket.protocol.http.WebApplication;

/**
 * A utilty class to provide access to the JSR-299 BeanManager
 * @author cpopetz
 *
 */
public class BeanManagerLookup
{

   private static BeanManager cachedManager;
   /**
    * This is the spec-defined name for the bean manager as registered in JNDI
    * See JSR-299 11.3
    */
   // TODO Change to java:comp/BeanManager when JBoss AS supports it
   private static String beanManagerJndiName = "java:app/BeanManager";

   public static void setBeanManagerJndiName(String beanManagerJndiName)
   {
      BeanManagerLookup.beanManagerJndiName = beanManagerJndiName;
   }

   public static String getBeanManagerJndiName()
   {
      return beanManagerJndiName;
   }

   /**
    * This is the name under which the bean manager will be stored in the
    * servlet context. This is not yet specified in JSR-299.
    */
   private static String beanManagerServletContextName = BeanManager.class.getName();

   public static void setBeanManagerServletContextName(String beanManagerServletContextName)
   {
      BeanManagerLookup.beanManagerServletContextName = beanManagerServletContextName;
   }

   public static String getBeanManagerServletContextName()
   {
      return beanManagerServletContextName;
   }
   

   /**
    * We will attempt first to obtain the BeanManager instance from JNDI.
    * Failing that, we will look in the servlet context.
    */
   public static BeanManager getBeanManager()
   {
      if (cachedManager == null)
      {
         // first look in jndi
         try
         {
            Context initialContext = new InitialContext();
            cachedManager = (BeanManager) initialContext.lookup(getBeanManagerJndiName());
         }
         catch (Exception e)
         {
            // we ignore this failure; it could mean we are operating in a
            // non-jndi (SE or Servlet) env
         }

         if (cachedManager == null)
         {
            ServletContext servletContext = ((WebApplication) Application.get()).getServletContext();
            if (servletContext != null)
            {
               cachedManager = (BeanManager) servletContext.getAttribute(getBeanManagerServletContextName());
            }
         }
      }
      return cachedManager;
   }
}
