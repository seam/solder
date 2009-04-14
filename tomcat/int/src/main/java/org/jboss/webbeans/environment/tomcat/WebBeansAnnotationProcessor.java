package org.jboss.webbeans.environment.tomcat;

import java.lang.reflect.InvocationTargetException;

import javax.naming.NamingException;

import org.apache.AnnotationProcessor;
import org.jboss.webbeans.manager.api.WebBeansManager;

public class WebBeansAnnotationProcessor implements AnnotationProcessor
{
   
   private final WebBeansManager manager;
   
   public WebBeansAnnotationProcessor(WebBeansManager manager)
   {
      this.manager = manager;
   }

   public void processAnnotations(Object instance) throws IllegalAccessException, InvocationTargetException, NamingException
   {
      manager.injectNonContextualInstance(instance);
   }

   public void postConstruct(Object arg0) throws IllegalAccessException, InvocationTargetException
   {
      // TODO Auto-generated method stub
      
   }
   
   public void preDestroy(Object arg0) throws IllegalAccessException, InvocationTargetException
   {
      // TODO Auto-generated method stub
      
   }
   
}
