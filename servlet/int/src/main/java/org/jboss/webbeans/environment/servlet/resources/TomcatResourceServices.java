package org.jboss.webbeans.environment.servlet.resources;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.jboss.webbeans.resources.spi.ResourceServices;
import org.jboss.webbeans.resources.spi.helpers.AbstractResourceServices;

public abstract class TomcatResourceServices extends AbstractResourceServices implements ResourceServices
{
   
   private Context context;
   
   public TomcatResourceServices()
   {
      try
      {
         context = new InitialContext();
      }
      catch (NamingException e)
      {
         throw new IllegalStateException("Error creating JNDI context", e);
      }
   }
   
   @Override
   protected Context getContext()
   {
      return context;
   }

}
