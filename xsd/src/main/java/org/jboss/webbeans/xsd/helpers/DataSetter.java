/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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

package org.jboss.webbeans.xsd.helpers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;

import org.jboss.webbeans.xsd.model.ClassModel;
import org.jboss.webbeans.xsd.model.FieldModel;
import org.jboss.webbeans.xsd.model.MethodModel;
import org.jboss.webbeans.xsd.model.ParameterModel;
import org.jboss.webbeans.xsd.model.TypedModel;

/**
 * Helper for examining classes and members and populating the model
 * 
 * @author Nicklas Karlsson
 * 
 */
public class DataSetter
{

   /**
    * Checks if an element is public
    * 
    * @param element The element to check
    * @return True if public, false otherwise
    */
   private static boolean isPublic(Element element)
   {
      return element.getModifiers().contains(Modifier.PUBLIC);
   }

   /**
    * Inspects a type element and populates a class model
    * 
    * @param classModel The class model to populate
    * @param element The element to inspect
    * @param parent The parent of the class
    */
   public static void populateClassModel(ClassModel classModel, Element element, ClassModel parent)
   {
      Map<String, Set<String>> annotations = getAnnotations(element);
      TypeElement typeElement = (TypeElement) element;
      classModel.setName(typeElement.getQualifiedName().toString());
      classModel.setParent(parent);
      classModel.setAnnotations(annotations);
   }

   /**
    * Inspects a field element and populates a class model
    * 
    * @param classModel The class model to populate
    * @param element The element to inspect
    */
   public static void populateFieldModel(ClassModel classModel, Element element)
   {
      if (!isPublic(element))
      {
         return;
      }
      String name = element.getSimpleName().toString();
      String type = element.asType().toString();
      boolean primitive = element.asType().getKind().isPrimitive();
      Map<String, Set<String>> annotations = getAnnotations(element);
      FieldModel field = new FieldModel();
      field.setName(name);
      field.setType(type);
      field.setPrimitive(primitive);
      field.setAnnotations(annotations);
      classModel.addField(field);
   }

   /**
    * Inspects a method or constructor and populates a class model
    * 
    * @param classModel The class model to populate
    * @param element The element to inspect
    */
   public static void populateMethodModel(ClassModel classModel, Element element)
   {
      if (!isPublic(element))
      {
         return;
      }
      ExecutableElement executableElement = (ExecutableElement) element;

      String name = element.getSimpleName().toString();
      
      TypedModel returnType = new TypedModel();
      returnType.setType(executableElement.getReturnType().toString());
      returnType.setPrimitive(executableElement.getReturnType().getKind().isPrimitive() || executableElement.getReturnType().getKind() == TypeKind.VOID);
      
      MethodModel method = new MethodModel();
      method.setName(name);
      method.setAnnotations(getAnnotations(executableElement));
      method.setReturnType(returnType);

      for (VariableElement parameterElement : executableElement.getParameters())
      {
         String paramName = parameterElement.getSimpleName().toString();
         String paramType = parameterElement.asType().toString();
         boolean paramPrimitive = parameterElement.asType().getKind().isPrimitive();
         Map<String, Set<String>> paramAnnotations = getAnnotations(parameterElement);
         ParameterModel parameter = new ParameterModel();
         parameter.setName(paramName);
         parameter.setType(paramType);
         parameter.setPrimitive(paramPrimitive);
         parameter.setAnnotations(paramAnnotations);
         method.addParameter(parameter);
      }
      // OK, cheating a little with a common model for methods and constructors
      if ("<init>".equals(name))
      {
         classModel.addConstructor(method);
      }
      else
      {
         classModel.addMethod(method);
      }
   }

   private static Map<String, Set<String>> getAnnotations(Element element)
   {
      Map<String, Set<String>> annotations = new HashMap<String, Set<String>>();
      for (AnnotationMirror annotation : element.getAnnotationMirrors())
      {
         Set<String> metaAnnotations = new HashSet<String>();
         for (AnnotationMirror metaAnnotation : annotation.getAnnotationType().asElement().getAnnotationMirrors())
         {
            metaAnnotations.add(metaAnnotation.getAnnotationType().toString());
         }
         annotations.put(annotation.getAnnotationType().toString(), metaAnnotations);
      }
      return annotations;
   }

}
