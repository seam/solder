package org.jboss.weld.extensions.test.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Method;
import java.net.URL;

import org.jboss.weld.extensions.properties.Properties;
import org.jboss.weld.extensions.properties.Property;
import org.junit.Test;

/**
 * Verify that only valid properties are permitted, as per the JavaBean specification.
 * 
 * @author Dan Allen
 * @see http://www.oracle.com/technetwork/java/javase/documentation/spec-136004.html
 */
public class PropertyFromMethodTest
{
   @Test
   public void testValidPropertyGetterMethod() throws Exception
   {
      Method getter = ClassToIntrospect.class.getMethod("getName");
      Property<String> p = Properties.createProperty(getter);
      assertNotNull(p);
      assertEquals("name", p.getName());
      assertEquals(getter, p.getMember());
   }
   
   @Test
   public void testValidPropertySetterMethod() throws Exception
   {
      Property<String> p = Properties.createProperty(ClassToIntrospect.class.getMethod("setName", String.class));
      assertNotNull(p);
      assertEquals("name", p.getName());
   }
   
   @Test
   public void testReadOnlyProperty() throws Exception
   {
      Property<String> p = Properties.createProperty(ClassToIntrospect.class.getMethod("getTitle"));
      assertNotNull(p);
      assertEquals("title", p.getName());
      assertTrue(p.isReadOnly());
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testEmptyPropertyGetterMethod() throws Exception
   {
      Properties.createProperty(ClassToIntrospect.class.getMethod("get"));
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testEmptyBooleanPropertyGetterMethod() throws Exception
   {
      Properties.createProperty(ClassToIntrospect.class.getMethod("is"));
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testNonPrimitiveBooleanPropertyIsMethod() throws Exception
   {
      Properties.createProperty(ClassToIntrospect.class.getMethod("isValid"));
   }
   
   @Test
   public void testSingleCharPropertyGetterMethod() throws Exception
   {
      Method getter = ClassToIntrospect.class.getMethod("getP");
      Property<String> p = Properties.createProperty(getter);
      assertNotNull(p);
      assertEquals("p", p.getName());
      assertEquals(getter, p.getMember());
   }
   
   @Test
   public void testSingleCharPropertySetterMethod() throws Exception
   {
      Property<String> p = Properties.createProperty(ClassToIntrospect.class.getMethod("setP", String.class));
      assertNotNull(p);
      assertEquals("p", p.getName());
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testGetterMethodWithVoidReturnType() throws Exception
   {
      Properties.createProperty(ClassToIntrospect.class.getMethod("getFooBar"));
   }
   
   @Test(expected = IllegalArgumentException.class)
   public void testSetterMethodWithMultipleParameters() throws Exception
   {
      Properties.createProperty(ClassToIntrospect.class.getMethod("setSalary", Double.class, Double.class));
   }
   
   @Test
   public void testAcronymProperty() throws Exception
   {
      Method getter = ClassToIntrospect.class.getMethod("getURL");
      Property<URL> p = Properties.createProperty(getter);
      assertNotNull(p);
      assertEquals("URL", p.getName());
      assertEquals(getter, p.getMember());
   }
}
