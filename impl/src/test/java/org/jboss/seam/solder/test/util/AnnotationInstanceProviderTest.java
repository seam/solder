/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.seam.solder.test.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jboss.seam.solder.reflection.AnnotationInstanceProvider;
import org.jboss.seam.solder.reflection.NullMemberException;
import org.junit.Test;

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
      SimpleAnnotation an = provider.get(SimpleAnnotation.class, Collections.<String, Object> emptyMap());
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
   @Test(expected = NullMemberException.class)
   public void testNullMemberException()
   {
      AnnotationInstanceProvider provider = new AnnotationInstanceProvider();
      Map<String, Object> values = new HashMap<String, Object>();
      values.put("value", Long.valueOf(1));
      values.put("someMember", null);
      IntMemberAnnotation an = provider.get(IntMemberAnnotation.class, values);

   }

   /**
    * Test that an Annotation will use the default values if a member with
    * default values is null
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
