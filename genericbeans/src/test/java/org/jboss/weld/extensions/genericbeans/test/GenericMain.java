package org.jboss.weld.extensions.genericbeans.test;

import org.jboss.weld.extensions.genericbeans.Generic;
import org.jboss.weld.extensions.genericbeans.InjectGeneric;

@Generic(TestAnnotation.class)
public class GenericMain
{
   @InjectGeneric
   GenericDep dep;

   public String getValue()
   {
      return dep.getValue();
   }

   public TestAnnotation getNoData()
   {
      return dep.getNoData();
   }
}
