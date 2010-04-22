package org.jboss.weld.test.extensions.managedproducer;

import javax.inject.Inject;

public class ManagedReciever
{
   @MPType("bean1")
   @Inject
   ProducedInterface bean1;

   @MPType("bean2")
   @Inject
   ProducedInterface bean2;

}
