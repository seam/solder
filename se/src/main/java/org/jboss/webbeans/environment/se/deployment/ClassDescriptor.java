/**
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
package org.jboss.webbeans.environment.se.deployment;

import org.jboss.webbeans.log.LogProvider;
import org.jboss.webbeans.log.Logging;

import java.net.URL;

public class ClassDescriptor extends FileDescriptor
{
   private static LogProvider log = Logging.getLogProvider(ClassDescriptor.class);
   private Class<?> clazz;
   
   public ClassDescriptor(String name, URL url, Class<?> clazz)
   {
      super(name, url);
      this.clazz = clazz;
   }
   
   public ClassDescriptor(String name, ClassLoader classLoader)
   {
      super(name, classLoader);
      
      String classname = filenameToClassname(name);
      log.trace("Trying to load class " + classname);
      
      try
      {
         clazz = classLoader.loadClass(classname);
         // IBM JVM will throw a TypeNotPresentException if any annotation on
         // the class is not on
         // the classpath, rendering the class virtually unusable (given Seam's
         // heavy use of annotations)
         clazz.getAnnotations();
      }
      catch (ClassNotFoundException cnfe)
      {
         log.info("could not load class: " + classname, cnfe);
      }
      catch (NoClassDefFoundError ncdfe)
      {
         log.debug("could not load class (missing dependency): " + classname, ncdfe);
      }
      catch (TypeNotPresentException tnpe)
      {
         clazz = null;
         log.debug("could not load class (annotation missing dependency): " + classname, tnpe);
      }
   }
   
   public Class<?> getClazz()
   {
      return clazz;
   }
   
   @Override
   public String toString()
   {
      return clazz.getName();
   }
   
   /**
    * Convert a path to a class file to a class name
    */
   public static String filenameToClassname(String filename)
   {
      return filename.substring(0, filename.lastIndexOf(".class")).replace('/', '.').replace('\\', '.');
   }
   
   @Override
   public boolean equals(Object other)
   {
      if (other instanceof ClassDescriptor)
      {
         ClassDescriptor that = (ClassDescriptor) other;
         
         return this.getClazz().equals(that.getClazz());
      }
      else
      {
         return false;
      }
   }
   
   @Override
   public int hashCode()
   {
      return getClazz().hashCode();
   }
}
