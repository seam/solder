package org.jboss.weld.extensions.test.autoproxy;

import javax.enterprise.inject.Default;
import javax.enterprise.util.AnnotationLiteral;

import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.weld.test.AbstractWeldTest;
import org.testng.annotations.Test;

@Artifact
public class AutoproxyTest extends AbstractWeldTest
{

   @Test
   public void testAutoProxy()
   {
      HelloBob bob = getReference(HelloBob.class, new AnnotationLiteral<Default>()
      {
      });
      assert bob.sayHello().equals("Hello Bob");
      HelloJane jane = getReference(HelloJane.class);
      assert jane.sayHello().equals("Hello Jane");
   }

}
