package org.jboss.weld.test.tomcat.lookup;

import javax.annotation.Resource;
import javax.enterprise.inject.spi.BeanManager;

public class Vole
{
   
   @Resource(mappedName="java:comp/env/app/Manager")
   BeanManager manager;
   
   public BeanManager getManager()
   {
      return manager;
   }
   
}
