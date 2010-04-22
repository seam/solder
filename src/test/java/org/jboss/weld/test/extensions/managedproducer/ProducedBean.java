package org.jboss.weld.test.extensions.managedproducer;

import org.jboss.weld.extensions.Veto;

@Veto
public class ProducedBean implements ProducedInterface
{
   String value = "wrong";

   public String getValue()
   {
      return value;
   }

   public void setValue(String value)
   {
      this.value = value;
   }
}
