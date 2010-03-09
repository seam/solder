package org.jboss.weld.test.extensions.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jboss.weld.extensions.util.AnnotationInstanceProvider;
import org.jboss.weld.extensions.util.NullMemberException;
import org.testng.annotations.Test;

/**
 * Test of dynamic annotation creation
 * 
 * @author Stuart Douglas
 * 
 */
public class AnnotationInstanceProviderTest
{

   /**
    * basic test to make sure the annotation creator can create an annotation
    */
   @Test
   public void testSimpleAnnotationCreation()
   {
      AnnotationInstanceProvider provider = new AnnotationInstanceProvider();
      SimpleAnnotation an = provider.get(SimpleAnnotation.class, Collections.<String, Object>emptyMap());
      assert an != null : "Annotation was null";
      assert an.annotationType() == SimpleAnnotation.class : "Annotation returned wrong result for annotationType()";
      SimpleAnnotation realAn = AnnotatedClass.class.getAnnotation(SimpleAnnotation.class);
      assert an.equals(realAn) : "Equality between declared annotation failed";
   }

   /**
    * Test with int members
    */
   @Test
   public void testIntMemberAnnotationCreation()
   {
      AnnotationInstanceProvider provider = new AnnotationInstanceProvider();
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("value", Long.valueOf(1));
      values.put("someMember", Integer.valueOf(0));
      IntMemberAnnotation an = provider.get(IntMemberAnnotation.class, values);
      assert an != null : "Annotation was null";
      assert an.annotationType() == IntMemberAnnotation.class : "Annotation returned wrong result for annotationType()";
      IntMemberAnnotation realAn = AnnotatedClass.class.getAnnotation(IntMemberAnnotation.class);
      assert an.equals(realAn) : "Equality between declared annotation failed";
   }

   /**
    * Test with int members
    */
   @Test
   public void testNotEqualIntMemberAnnotationCreation()
   {
      AnnotationInstanceProvider provider = new AnnotationInstanceProvider();
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("value", Long.valueOf(6));
      values.put("someMember", Integer.valueOf(0));
      IntMemberAnnotation an = provider.get(IntMemberAnnotation.class, values);
      assert an != null : "Annotation was null";
      assert an.annotationType() == IntMemberAnnotation.class : "Annotation returned wrong result for annotationType()";
      IntMemberAnnotation realAn = AnnotatedClass.class.getAnnotation(IntMemberAnnotation.class);
      assert !an.equals(realAn) : "Equality between declared annotation failed, annotations were not equal but equals returned true";
   }

   /**
    * Test with multiple members
    */
   @Test
   public void testMultipleMemberAnnotationCreation()
   {
      AnnotationInstanceProvider provider = new AnnotationInstanceProvider();
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("intMember", 1);
      values.put("longMember", 1);
      values.put("shortMember", 1);
      values.put("floatMember", 0);
      values.put("doubleMember", 0);
      values.put("byteMember", ((byte) 1));
      values.put("charMember", 'c');
      values.put("booleanMember", true);
      values.put("intArrayMember", new int[] { 0, 1 });
      MultipleMembers an = provider.get(MultipleMembers.class, values);
      assert an != null : "Annotation was null";
      assert an.annotationType() == MultipleMembers.class : "Annotation returned wrong result for annotationType()";
      MultipleMembers realAn = AnnotatedClass.class.getAnnotation(MultipleMembers.class);
      assert an.equals(realAn) : "Equality between declared annotation failed";
   }

   /**
    * Test that an exception is thrown when a member is null
    */
   @Test(expectedExceptions = NullMemberException.class)
   public void testNullMemberException()
   {
      AnnotationInstanceProvider provider = new AnnotationInstanceProvider();
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("value", Long.valueOf(1));
      values.put("someMember", null);
      IntMemberAnnotation an = provider.get(IntMemberAnnotation.class, values);

   }

   /**
    * Test that an Annotation will use the default values if a member with default 
    * values is null
    */
   @Test
   public void testDefaultValue()
   {
      AnnotationInstanceProvider provider = new AnnotationInstanceProvider();
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("someMember", Integer.valueOf(0));
      IntMemberAnnotation an = provider.get(IntMemberAnnotation.class, values);
      assert an != null : "Annotation was null";
      assert an.value() == 1 : "Annotation member was not equal to default value";
   }

}
