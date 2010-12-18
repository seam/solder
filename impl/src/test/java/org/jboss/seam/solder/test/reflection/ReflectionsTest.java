package org.jboss.seam.solder.test.reflection;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import junit.framework.Assert;

import org.jboss.seam.solder.reflection.Reflections;
import org.jboss.seam.solder.test.reflection.model.Cat;
import org.junit.Before;
import org.junit.Test;

/**
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public class ReflectionsTest
{
   private Cat subject;
   
   @Before
   public void setupFixture()
   {
      subject = new Cat();
   }
   
   @Test
   public void should_invoke_public_no_args_method()
   {
      Method m = Reflections.findDeclaredMethod(Cat.class, "chewOnPowerCord");
      Reflections.invokeMethod(m, subject);
      Assert.assertEquals(8, subject.getLives());
   }
   
   @Test
   public void should_invoke_public_method_with_args()
   {
      Method m = Reflections.findDeclaredMethod(Cat.class, "earnLives", Integer.TYPE);
      Reflections.invokeMethod(m, subject, 1);
      Assert.assertEquals(10, subject.getLives());
   }
   
   @Test(expected = RuntimeException.class)
   public void should_fail_invoking_public_method_with_invalid_args()
   {
      Method m = Reflections.findDeclaredMethod(Cat.class, "earnLives", Integer.TYPE);
      Reflections.invokeMethod(m, subject, -1);
      Assert.assertEquals(10, subject.getLives());
   }
   
   @Test(expected = RuntimeException.class)
   public void should_fail_invoking_private_method()
   {
      Method m = Reflections.findDeclaredMethod(Cat.class, "diveUnderMovingCar");
      Reflections.invokeMethod(m, subject);
   }
   
   @Test
   public void should_invoke_private_method_when_set_accessible()
   {
      Method m = Reflections.findDeclaredMethod(Cat.class, "diveUnderMovingCar");
      Reflections.invokeMethod(true, m, subject);
   }
   
   
   @Test(expected = RuntimeException.class)
   public void should_fail_invoking_package_method()
   {
      Method m = Reflections.findDeclaredMethod(Cat.class, "fightWithBigDog");
      Reflections.invokeMethod(m, subject);
   }
   
   @Test
   public void should_invoke_package_method_when_set_accessible()
   {
      Method m = Reflections.findDeclaredMethod(Cat.class, "fightWithBigDog");
      Reflections.invokeMethod(true, m, subject);
   }
   
   @Test(expected = RuntimeException.class)
   public void should_fail_setting_private_field()
   {
      Field f = Reflections.findDeclaredField(Cat.class, "lives");
      Reflections.setFieldValue(f, subject, 1);
   }
   
   @Test
   public void should_set_private_field_when_set_accessible()
   {
      Field f = Reflections.findDeclaredField(Cat.class, "lives");
      Reflections.setFieldValue(true, f, subject, 1);
      Assert.assertEquals(1, subject.getLives());
   }
  
   @Test
   public void should_set_method_accessible()
   {
      Method m = Reflections.findDeclaredMethod(Cat.class, "fightWithBigDog");
      Assert.assertFalse(m.isAccessible());
      Method result = Reflections.setAccessible(m);
      Assert.assertTrue(m.isAccessible());
      Assert.assertTrue(result.isAccessible());
      Assert.assertSame(m, result);
   }
   
   @Test
   public void should_set_field_accessible()
   {
      Field f = Reflections.findDeclaredField(Cat.class, "lives");
      Assert.assertFalse(f.isAccessible());
      Field result = Reflections.setAccessible(f);
      Assert.assertTrue(f.isAccessible());
      Assert.assertTrue(result.isAccessible());
      Assert.assertSame(f, result);
   }
}
