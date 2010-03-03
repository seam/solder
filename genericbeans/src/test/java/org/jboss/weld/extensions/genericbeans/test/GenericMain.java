package org.jboss.weld.extensions.genericbeans.test;

import javax.inject.Inject;

import org.jboss.weld.extensions.genericbeans.Generic;

@Generic(TestAnnotation.class)
public class GenericMain
{
   @Inject
   public GenericMain(GenericConstructorArgument args)
   {
      constArgs = args;
   }

   @Inject
   GenericDep dep;

   @Inject
   NormalBean normalBean;

   GenericConstructorArgument constArgs;

   public String getValue()
   {
      return dep.getValue();
   }

   public TestAnnotation getNoData()
   {
      return dep.getNoData();
   }
}
