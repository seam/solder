package org.jboss.weld.extensions.test.autoproxy;

import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.impl.base.asset.ByteArrayAsset;
import org.jboss.weld.extensions.autoproxy.AutoProxyExtension;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AutoproxyTest
{
   @Deployment
   public static Archive<?> deploy()
   {
      JavaArchive a = ShrinkWrap.create("test.jar", JavaArchive.class);
      a.addPackage(AutoproxyTest.class.getPackage());
      a.addPackage(AutoProxyExtension.class.getPackage());
      a.addManifestResource(new ByteArrayAsset(new byte[0]), "beans.xml");
      return a;
   }

   @Inject
   HelloBob bob;

   @Inject
   HelloJane jane;

   @Test
   public void testAutoProxy()
   {
      assert bob.sayHello().equals("Hello Bob");
      assert jane.sayHello().equals("Hello Jane");
   }

}
