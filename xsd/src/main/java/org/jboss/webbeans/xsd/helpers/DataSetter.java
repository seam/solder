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

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;

import org.jboss.webbeans.xsd.model.ClassModel;
import org.jboss.webbeans.xsd.model.MethodModel;
import org.jboss.webbeans.xsd.model.NamedModel;
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
      NamedModel field = new NamedModel(element.getSimpleName().toString());
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

      MethodModel method = new MethodModel(element.getSimpleName().toString());

      for (VariableElement parameterElement : executableElement.getParameters())
      {
         boolean primitive = parameterElement.asType().getKind().isPrimitive();
         TypedModel parameter = new TypedModel(parameterElement.asType().toString(), primitive);
         method.addParameter(parameter);
      }
      // OK, cheating a little with a common model for methods and constructors
      if ("<init>".equals(method.getName()))
      {
         method.setName(classModel.getSimpleName());
         classModel.addConstructor(method);
      }
      else
      {
         classModel.addMethod(method);
      }
   }

}
