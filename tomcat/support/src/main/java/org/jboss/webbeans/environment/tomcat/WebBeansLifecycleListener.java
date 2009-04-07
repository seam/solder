package org.jboss.webbeans.environment.tomcat;

import java.lang.reflect.InvocationTargetException;

import javax.naming.NamingException;
import javax.servlet.ServletContext;

import org.apache.AnnotationProcessor;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;
import org.apache.catalina.core.StandardContext;

public class WebBeansLifecycleListener implements LifecycleListener
{
   
   public void lifecycleEvent(LifecycleEvent event)
   {
      if (event.getType().equals("after_start") && event.getLifecycle() instanceof StandardContext)
      {
         StandardContext context = (StandardContext) event.getLifecycle();
         final ServletContext servletContext = context.getServletContext();
         final AnnotationProcessor annotationProcessor = context.getAnnotationProcessor();
         context.setAnnotationProcessor(new ForwardingAnnotationProcessor()
         {

            @Override
            protected AnnotationProcessor delegate()
            {
               return annotationProcessor;
            }
            
            @Override
            public void processAnnotations(Object instance) throws IllegalAccessException, InvocationTargetException, NamingException
            {
               super.processAnnotations(instance);
               Object o = servletContext.getAttribute("org.jboss.webbeans.environment.tomcat.WebBeansAnnotationProcessor");
               if (o != null)
               {
                  AnnotationProcessor processor = (AnnotationProcessor) o;
                  processor.processAnnotations(instance);
               }
            }
            
         });
      }
   }
   
}
