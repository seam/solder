package org.jboss.weld.test.extensions.genericbeans;

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
