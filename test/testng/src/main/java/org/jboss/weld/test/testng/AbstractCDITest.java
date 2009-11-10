package org.jboss.weld.test.testng;

import org.jboss.weld.test.core.TestCore;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

public class AbstractCDITest
{
   private TestCore core;

   @BeforeSuite
   public void create() throws Exception
   {
      core = new TestCore();
      core.start();
   }

   @BeforeTest
   public void injectParameters() throws Exception
   {
      core.injectFields(this);
   }

   @AfterSuite
   public void destory()
   {
      core.stop();
   }
}
