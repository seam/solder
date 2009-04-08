package org.jboss.webbeans.test.tomcat.lookup;

import javax.inject.manager.Manager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class Mouse
{
   
   public Manager getManager()
   {
      try
      {
         return (Manager) new InitialContext().lookup("java:comp/env/app/Manager");
      }
      catch (NamingException e)
      {
         throw new RuntimeException(e);
      }
   }
   
}
