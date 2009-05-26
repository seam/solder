package org.jboss.webbeans.test.tomcat.lookup;

import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Mouse
{
   
   public BeanManager getManager()
   {
      try
      {
         return (BeanManager) new InitialContext().lookup("java:comp/env/app/Manager");
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }
   
}
