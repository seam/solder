package org.jboss.weld.extensions.genericbeans.test;

import javax.inject.Inject;

public class InjectedBean
{
   @Inject
   @SomeQualifier(1)
   public GenericMain main1;

   @Inject
   @SomeQualifier(2)
   public GenericMain main2;

}
