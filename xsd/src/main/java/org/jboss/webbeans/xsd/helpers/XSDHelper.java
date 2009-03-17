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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.processing.Filer;
import javax.tools.StandardLocation;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.jboss.webbeans.xsd.Schema;
import org.jboss.webbeans.xsd.model.ClassModel;

/**
 * Helper for XSD related operations
 * 
 * @author Nicklas Karlsson
 * 
 */
public class XSDHelper
{

   // The filed of the annotation processing environment
   private Filer filer;
   // The cache of already processed classes
   private Map<String, ClassModel> classModelCache = new HashMap<String, ClassModel>();
   // The XSD documents of the affected packages
   private Map<String, Schema> schemaMap = new HashMap<String, Schema>();

   /**
    * Creates a new helper
    * 
    * @param filer The filer of the processing environment
    */
   public XSDHelper(Filer filer)
   {
      this.filer = filer;
   }

   /**
    * Reads package info
    * 
    * @param packageName The package name
    * @return The package info of the package
    * @throws DocumentException If the schema could not be parsed
    * @throws IOException If the schema could not be read
    */
   private Schema getSchema(String packageName) throws DocumentException, IOException
   {
      Schema schema = new Schema(packageName);
      Document document = readSchema(packageName);
      schema.setDocument(document != null ? document : createSchema(packageName));
      return schema;
   }

   /**
    * Creates a new schema document
    * 
    * @param packageName The package name of the schema
    * @return The document
    */
   private Document createSchema(String packageName)
   {
      Document packageXSD = DocumentHelper.createDocument();
      packageXSD.addElement("Package");
      return packageXSD;
   }

   /**
    * Reads a schema for a package
    * 
    * @param packageName The package name
    * @return The schema document
    * @throws DocumentException If the document could not be parsed
    * @throws IOException If the document could not be read
    */
   private Document readSchema(String packageName) throws DocumentException, IOException
   {
      InputStream in = null;
      try
      {
         in = filer.getResource(StandardLocation.CLASS_OUTPUT, packageName, "schema.xsd").openInputStream();
         return new SAXReader().read(in);
      }
      catch (IOException e)
      {
         return null;
      }
      finally
      {
         if (in != null)
         {
            in.close();
         }
      }
   }

   /**
    * Writes package info to the disk
    * 
    * @param schema The package info to store
    */
   private void writePackageInfo(Schema schema)
   {
      try
      {
         writeSchema(schema.getPackageName(), schema.getDocument());
      }
      catch (IOException e)
      {
         throw new RuntimeException("Could not write schema for " + schema.getPackageName());
      }
   }

   /**
    * Writes a schema to disk
    * 
    * @param packageName The package name
    * @param schema The schema
    * @throws IOException If the file could not be written
    */
   private void writeSchema(String packageName, Document schema) throws IOException
   {
      OutputStream out = null;
      try
      {
         OutputFormat format = OutputFormat.createPrettyPrint();
         out = filer.createResource(StandardLocation.CLASS_OUTPUT, packageName, "schema.xsd").openOutputStream();
         XMLWriter writer = new XMLWriter(out, format);
         writer.write(schema);
         writer.flush();
         writer.close();
      }
      finally
      {
         if (out != null)
         {
            out.close();
         }
      }
   }

   /**
    * Updates the schemas for the affected packages
    * 
    * @param classModels The list of class models in the batch
    */
   public void updateSchemas(List<ClassModel> classModels)
   {
      for (ClassModel classModel : classModels)
      {
         String packageName = classModel.getPackage();
         Schema schema = schemaMap.get(packageName);
         if (schema == null)
         {
            try
            {
               schema = getSchema(packageName);
            }
            catch (DocumentException e)
            {
               throw new RuntimeException("Could not parse schema for package " + packageName);
            }
            catch (IOException e)
            {
               throw new RuntimeException("Could not read schema for package " + packageName);
            }
            schemaMap.put(packageName, schema);
         }
         updateClassInSchema(classModel, schema);
      }
   }

   /**
    * Writes the schemas back to disk
    */
   public void writeSchemas()
   {
      for (Schema schema : schemaMap.values())
      {
         // TODO: dummy, remove
         schema.refreshNamespaces();
         System.out.println(schema.getPackageName() + " (" + schema.getNamespaces() + ")");
         System.out.println(schema.getTypeReferences());
         writePackageInfo(schema);
      }
   }

   /**
    * Updates a schema with XSD from a file model
    * 
    * @param schema The schema
    * @param classModel The class model
    */
   private void updateClassInSchema(ClassModel classModel, Schema schema)
   {
      Document document = schema.getDocument();
      Node oldClassModel = document.selectSingleNode("//" + classModel.getSimpleName());
      if (oldClassModel != null)
      {
         // Remove the old class definition
         document.getRootElement().remove(oldClassModel);
      }
      // Create a new one
      document.getRootElement().addElement(classModel.getSimpleName());
      schema.addTypeReferences(classModel.getTypeReferences());
   }

   /**
    * Gets a cached class model
    * 
    * @param FQN The FQN of the class
    * @return The class model (or null if not cached)
    */
   public ClassModel getCachedClassModel(String FQN)
   {
      return classModelCache.get(FQN);
   }

   /**
    * Puts a class model in the cache
    * 
    * @param classModel The class model
    */
   public void cacheClassModel(ClassModel classModel)
   {
      classModelCache.put(classModel.getName(), classModel);
   }
}
