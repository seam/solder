package org.jboss.weld.test.extensions.managedproducer;

import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.weld.test.AbstractWeldTest;
import org.testng.annotations.Test;

@Artifact
public class ManagedProducerTest extends AbstractWeldTest
{
   @Test
   public void testManagedProducers()
   {
      ManagedReciever bean = getReference(ManagedReciever.class);
      assert bean.bean1.getValue().equals("bean1") : " value: " + bean.bean1.getValue();
      assert bean.bean2.getValue().equals("bean2") : " value: " + bean.bean1.getValue();
   }
}
