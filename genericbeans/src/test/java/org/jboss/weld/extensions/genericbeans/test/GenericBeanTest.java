package org.jboss.weld.extensions.genericbeans.test;

import org.testng.annotations.Test;

public class GenericBeanTest extends AbstractTest
{
   @Test
   public void testGeneric()
   {
      InjectedBean bean = getReference(InjectedBean.class);
      assert bean.main1.getValue().equals("hello1");
      assert bean.main2.getValue().equals("hello2");
      assert bean.main1.getNoData() == null;
      assert bean.main2.getNoData() == null;

   }
}
