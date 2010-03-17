package org.jboss.weld.test.extensions.genericbeans;

import javax.inject.Inject;

import org.jboss.weld.extensions.genericbeans.Generic;

@Generic(TestAnnotation.class)
public class GenericDep
{
   @Inject
   TestAnnotation data;

   TestAnnotation noData;

   public String getValue()
   {
      return data.value();
   }

   public TestAnnotation getNoData()
   {
      return noData;
   }
}
