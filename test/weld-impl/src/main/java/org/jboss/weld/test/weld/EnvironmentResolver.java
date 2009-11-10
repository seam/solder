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
package org.jboss.weld.test.weld;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Helper class to find the appropriate environment to 'bootstrap'
 * 
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class EnvironmentResolver
{
   public Environment resolve() throws Exception
   {
      Environment environment = findSupported(
//            new EEEnvironment(),
//            new PreviouslyInitiatedEnvironment(), 
            new SEEnvironment());
      if (environment == null)
      {
         throw new Exception(
               "No appropriate environment found. "
                     + "The test is running in a non EE/Servlet environment and the Webbeans SE Extension is missing from classpath");
      }
      return environment;
   }

   private Environment findSupported(Environment... environments)
   {
      for (Environment environment : environments)
      {
         if (environment.isSupported())
         {
            return environment;
         }
      }
      return null;
   }

   public static abstract class Environment
   {

      boolean isSupported()
      {
         try
         {
            if(getBeanManager() != null) {
               return true;
            }
         } catch (Exception e)
         {
         }
         return false;
      }

      void initialize() throws Exception
      {
      }

      void stop()
      {
      }

      abstract BeanManager getBeanManager() throws Exception;
   }

   public static class SEEnvironment extends Environment
   {
      private static final String SE_EXTENTION_CLASSNAME = "org.jboss.weld.environment.se.StartMain";
      private static final String SE_EXTENTION_CLASSNAME_METHOD = "go";
      private static final String SE_EXTENTION_BOOSTRAP = "org.jboss.weld.environment.se.ShutdownManager";
      private static final String SE_EXTENTION_BOOSTRAP_METHOD = "shutdown";

      private boolean initialized = false;
      private BeanManager manager;
      
      @Override
      boolean isSupported()
      {
         try
         {
            Thread.currentThread().getContextClassLoader().loadClass(
                  SE_EXTENTION_CLASSNAME);
            return true;
         } catch (Exception e)
         {
            return false;
         }
      }

      @Override
      void initialize() throws Exception
      {
         if (!initialized)
         {
            Class<?> seExtensionMainClass = Thread.currentThread().getContextClassLoader()
                    .loadClass(SE_EXTENTION_CLASSNAME);

            Method seExtensionStartMethod = seExtensionMainClass.getMethod(SE_EXTENTION_CLASSNAME_METHOD);
            Constructor<?> seExtensionConstructor = seExtensionMainClass.getConstructor(String[].class);
            Object seExtension = seExtensionConstructor.newInstance((Object)new String[0]);
            manager = (BeanManager)seExtensionStartMethod.invoke(seExtension);
            initialized = true;
         }
      }

      @Override
      BeanManager getBeanManager() throws Exception
      {
         if (!initialized)
         {
            throw new Exception("Environment has not been initialized.");
         }
         return manager;
      }

      @Override
      void stop()
      {
         try
         {
            Class<?> bootstrapClass = Thread.currentThread().getContextClassLoader()
                  .loadClass(SE_EXTENTION_BOOSTRAP);
            
            Method shutdownMethod = bootstrapClass.getMethod(SE_EXTENTION_BOOSTRAP_METHOD);
            Object bootstrap = getInstanceByType(manager, bootstrapClass);
            
            shutdownMethod.invoke(bootstrap);
         } 
         catch (Exception e)
         {
            throw new RuntimeException(
                  "Could load bootstrap[" + SE_EXTENTION_BOOSTRAP + "] class for shutdown", e);
         }
      }
   }

//   public static class EEEnvironment extends Environment
//   {
//      private static final String EE_CONTEXT_NAME = "java:comp/BeanManager";
//
//      @Override
//      BeanManager getBeanManager() throws Exception
//      {
//         InitialContext context = new InitialContext();
//         return (BeanManager) context.lookup(EE_CONTEXT_NAME);
//      }
//   }

//   public static class PreviouslyInitiatedEnvironment extends Environment
//   {
//      @Override
//      BeanManager getBeanManager() throws Exception
//      {
//         return null; // CurrentManager.rootManager();
//      }
//   }

   @SuppressWarnings("unchecked")
   public static <T> T getInstanceByType(BeanManager manager, Class<T> type, Annotation... bindings)
   {
      final Bean<?> bean = manager.getBeans(type).iterator().next();
      CreationalContext cc = manager.createCreationalContext(bean);
      return (T) manager.getReference(bean, type, cc);
   }

}
