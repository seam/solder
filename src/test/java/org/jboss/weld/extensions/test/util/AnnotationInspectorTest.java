package org.jboss.weld.extensions.test.util;

import org.jboss.testharness.impl.packaging.Artifact;
import org.jboss.weld.extensions.util.AnnotationInspector;
import org.jboss.weld.test.AbstractWeldTest;
import org.testng.annotations.Test;

@Artifact
public class AnnotationInspectorTest extends AbstractWeldTest
{
   
   @Test
   public void testAnnotationOnElement() throws Exception
   {
      assert AnnotationInspector.isAnnotationPresent(Animals.class.getMethod("dog"), Animal.class, false, getCurrentManager());
      assert AnnotationInspector.getAnnotation(Animals.class.getMethod("dog"), Animal.class, false, getCurrentManager()).species().equals("Dog");
   }
   
   @Test
   public void testAnnotationOnStereotype() throws Exception
   {
      assert AnnotationInspector.isAnnotationPresent(Animals.class.getMethod("cat"), Animal.class, true, getCurrentManager());
      assert AnnotationInspector.getAnnotation(Animals.class.getMethod("cat"), Animal.class, true, getCurrentManager()).species().equals("Cat");
   }

}
