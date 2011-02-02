package org.jboss.seam.solder.test.compat.alternative;

import javax.enterprise.inject.spi.Extension;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;
import static org.junit.Assert.assertEquals;

/**
 * This test verifies that alternatives correctly in BDAs that contains an Extension.
 * 
 * http://java.net/jira/browse/GLASSFISH-15791
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 *
 */
@RunWith(Arquillian.class)
public class AlternativeTest
{
   @Inject
   private Foo foo;
   
   @Deployment
   public static WebArchive getDeployment()
   {
      return create(WebArchive.class, "test.war")
         .addWebResource(EmptyAsset.INSTANCE, "beans.xml")
         .addLibrary(getJar());
   }
   
   public static JavaArchive getJar()
   {
      return create(JavaArchive.class, "test.jar")
         .addClasses(Foo.class, Bar.class, BarAlternative.class, NoopExtension.class)
         .addManifestResource("org/jboss/seam/solder/test/compat/alternative/beans.xml", "beans.xml")
         .addServiceProvider(Extension.class, NoopExtension.class);
   }
   
   @Test
   public void testAlternative()
   {
      assertEquals("barAlternative", foo.getBar().ping());
   }
}
