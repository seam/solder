package org.jboss.weld.extensions.genericbeans.test;

import javax.enterprise.inject.Produces;

public class GenericProducer
{
   @SomeQualifier(1)
   @Produces
   @TestAnnotation("hello1")
   GenericMain main1;

   @SomeQualifier(2)
   @Produces
   @TestAnnotation("hello2")
   GenericMain main2;

}
