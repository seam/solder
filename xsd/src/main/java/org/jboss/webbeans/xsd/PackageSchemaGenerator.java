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

package org.jboss.webbeans.xsd;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementFilter;

import org.dom4j.DocumentException;
import org.jboss.webbeans.xsd.helpers.DataSetter;
import org.jboss.webbeans.xsd.model.ClassModel;

/**
 * An annotation processor that updates the package-level XSD for the packages
 * that have had their files compiled.
 * 
 * @author Nicklas Karlsson
 * 
 */
@SupportedSourceVersion(SourceVersion.RELEASE_6)
@SupportedAnnotationTypes("*")
public class PackageSchemaGenerator extends AbstractProcessor
{
   private Map<String, ClassModel> classModelCache;
   private Map<String, Schema> schemas;

   @Override
   public synchronized void init(ProcessingEnvironment processingEnvironment)
   {
      super.init(processingEnvironment);
      classModelCache = new HashMap<String, ClassModel>();
      schemas = new HashMap<String, Schema>();
   }

   @Override
   public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment)
   {
      for (Element element : roundEnvironment.getRootElements())
      {
         if (ElementKind.CLASS.equals(element.getKind()) || ElementKind.ANNOTATION_TYPE.equals(element.getKind()))
         {
            ClassModel classModel = inspectClass(element);
            try
            {
               addClassToSchema(classModel);
            }
            catch (DocumentException e)
            {
               // TODO: real logging
               System.out.println("Could not read or create schema for " + classModel.getPackage());
            }
         }
      }
      if (!roundEnvironment.processingOver())
      {
         for (Schema schema : schemas.values())
         {
            try
            {
               schema.rebuild().write(processingEnv.getFiler());
            }
            catch (IOException e)
            {
               // TODO: real logging
               System.out.println("Could not write schema.xsd for " + schema);
            }
         }
      }
      return false;
   }

   private void addClassToSchema(ClassModel classModel) throws DocumentException
   {
      String packageName = classModel.getPackage();
      Schema schema = schemas.get(packageName);
      if (schema == null)
      {
         schema = Schema.of(packageName, classModel.getPackageElement(), processingEnv.getFiler());
         schemas.put(packageName, schema);
      }
      schema.addClass(classModel);
   }

   /**
    * Creates a class model from a class element
    * 
    * @param element The element to analyze
    * @return The class model
    */
   private ClassModel inspectClass(Element element)
   {
      TypeElement typeElement = (TypeElement) element;
      ClassModel classModel = ClassModel.of(typeElement, processingEnv.getElementUtils().getPackageOf(typeElement));

      // If the class has superclass's, scan them recursively
      if (typeElement.getSuperclass().getKind() != TypeKind.NONE)
      {
         inspectClass(((DeclaredType) typeElement.getSuperclass()).asElement());
      }

      // Gets the parent from the cache. We know it's there since we has scanned
      // the hierarchy already
      classModel.setParent(classModelCache.get(typeElement.getSuperclass().toString()));
      // Filter out the fields and populate the model
      for (Element field : ElementFilter.fieldsIn(element.getEnclosedElements()))
      {
         DataSetter.populateFieldModel(classModel, field);
      }
      // Filter out the methods and populate the model
      for (Element method : ElementFilter.methodsIn(element.getEnclosedElements()))
      {
         DataSetter.populateMethodModel(classModel, method);
      }
      // Filter out the constructors and populate the model
      for (Element constructor : ElementFilter.constructorsIn(element.getEnclosedElements()))
      {
         DataSetter.populateConstructorModel(classModel, constructor);
      }
      // Place the new class model in the cache
      classModelCache.put(classModel.getName(), classModel);
      return classModel;
   }

}
