package org.jboss.weld.extensions.beanid;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
/**
 * extension that allows the AnnotatedType to be retrieved for a given bean 
 * 
 * This is hopefully a temporary workaround until a spec limitation is removed
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 *
 */
public class IdentifiableBeanExtension implements Extension
{
   Map<Object, AnnotatedType<?>> types = Collections.synchronizedMap(new WeakHashMap<Object, AnnotatedType<?>>(1000));

   public void processInjectionTarget(@Observes ProcessInjectionTarget<?> event)
   {
      boolean requiresId = false;
      for(Annotation a : event.getAnnotatedType().getAnnotations())
      {
         if(a.annotationType().isAnnotationPresent(RequiresIdentification.class))
         {
            requiresId = true;
            break;
         }
      }
      if(!requiresId)
      {
         for(AnnotatedMethod<?> m : event.getAnnotatedType().getMethods())
         {
            for(Annotation a  : m.getAnnotations())
            {
               if(a.annotationType().isAnnotationPresent(RequiresIdentification.class))
               {
                  requiresId = true;
                  break;
               }
            }
         }
      }
      if(requiresId)
      {
         event.setInjectionTarget(new IdentifiableInjectionTarget(event.getInjectionTarget(), event.getAnnotatedType(),types));
      }
   }
   
   public AnnotatedType<?> getAnnotatedType(Object instance)
   {
      return types.get(instance);
   }
   
}
