package org.jboss.webbeans.test.tomcat.lookup;

import javax.annotation.Resource;
import javax.inject.manager.Manager;

public class Vole
{
   
   @Resource(mappedName="java:comp/env/app/Manager")
   Manager manager;
   
   public Manager getManager()
   {
      return manager;
   }
   
}
