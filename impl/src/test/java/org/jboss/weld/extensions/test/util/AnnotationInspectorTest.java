package org.jboss.weld.extensions.test.util;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.weld.extensions.util.AnnotationInspector;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AnnotationInspectorTest
{
   @Deployment
   public static Archive<?> deploy()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar").addPackage(AnnotationInspectorTest.class.getPackage());
   }

   @Inject
   BeanManager beanManager;

   @Test
   public void testAnnotationOnElement() throws Exception
   {
      assert AnnotationInspector.isAnnotationPresent(Animals.class.getMethod("dog"), Animal.class, false, beanManager);
      assert AnnotationInspector.getAnnotation(Animals.class.getMethod("dog"), Animal.class, false, beanManager).species().equals("Dog");
   }

   @Test
   public void testAnnotationOnStereotype() throws Exception
   {
      assert AnnotationInspector.isAnnotationPresent(Animals.class.getMethod("cat"), Animal.class, true, beanManager);
      assert AnnotationInspector.getAnnotation(Animals.class.getMethod("cat"), Animal.class, true, beanManager).species().equals("Cat");
   }

}
