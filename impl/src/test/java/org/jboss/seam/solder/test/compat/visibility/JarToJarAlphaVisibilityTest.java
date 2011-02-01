package org.jboss.seam.solder.test.compat.visibility;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.jboss.shrinkwrap.api.ShrinkWrap.create;

/**
 * Verifies that a bean from a BDA is injectable into another BDA.
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 */
@RunWith(Arquillian.class)
public class JarToJarAlphaVisibilityTest
{
   @Deployment
   public static WebArchive getDeployment()
   {
      WebArchive war = ShrinkWrap.create(WebArchive.class, "test.war");
      war.addWebResource(EmptyAsset.INSTANCE, "beans.xml");
      war.addLibrary(create(JavaArchive.class, "alpha.jar").addClass(Foo.class).addManifestResource(EmptyAsset.INSTANCE, "beans.xml"));
      war.addLibrary(create(JavaArchive.class, "bravo.jar").addClass(Bar.class).addManifestResource(EmptyAsset.INSTANCE, "beans.xml"));
      return war;
   }
   
   @Test
   public void testDeployment()
   {
      // noop
   }
}
