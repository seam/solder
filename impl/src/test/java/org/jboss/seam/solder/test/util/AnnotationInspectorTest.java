package org.jboss.seam.solder.test.util;

import static org.jboss.seam.solder.test.util.Deployments.baseDeployment;

import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;

import org.jboss.arquillian.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.seam.solder.reflection.AnnotationInspector;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(Arquillian.class)
public class AnnotationInspectorTest
{
   @Deployment
   public static Archive<?> deployment()
   {
      return baseDeployment().addPackage(AnnotationInspectorTest.class.getPackage());
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
