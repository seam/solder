/**
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.webbeans.environment.se.discovery;

import java.net.URL;
import org.jboss.webbeans.log.LogProvider;
import org.jboss.webbeans.log.Logging;

/**
 * Abstract base class for {@link Scanner} providing common functionality
 * 
 * This class provides file-system orientated scanning
 * 
 * @author Pete Muir
 * 
 */
public abstract class AbstractScanner implements Scanner
{
   
   private static final LogProvider log = Logging.getLogProvider(Scanner.class);
   private final ClassLoader classLoader;
   private final SEWebBeanDiscovery webBeanDiscovery;
   
   public AbstractScanner(ClassLoader classLoader, SEWebBeanDiscovery webBeanDiscovery)
   {
      this.classLoader = classLoader;
      this.webBeanDiscovery = webBeanDiscovery;
   }
   
   protected void handle(String name, URL url)
   {
      if (name.endsWith(".class"))
      {
         String className = filenameToClassname(name);
         try
         {
            webBeanDiscovery.getWbClasses().add(getClassLoader().loadClass(className));
         }
         catch (NoClassDefFoundError e)
         {
            log.error("Error loading " + name, e);
         }
         catch (ClassNotFoundException e)
         {
            log.error("Error loading " + name, e);
         }
      }
      else if (name.endsWith("beans.xml"))
      {
         webBeanDiscovery.getWbUrls().add(url);
      }
   }
   
   public ClassLoader getClassLoader()
   {
      return classLoader;
   }
   
   /**
    * Convert a path to a class file to a class name
    */
   public static String filenameToClassname(String filename)
   {
      return filename.substring( 0, filename.lastIndexOf(".class") )
            .replace('/', '.').replace('\\', '.');
   }
   
}
