package org.jboss.weld.extensions.beanid;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessInjectionTarget;
/**
 * extension that allows the AnnotatedType to be retrieved for a given bean 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 *
 */
public class IdentifiableBeanExtension implements Extension
{
   private AtomicLong currentId = new AtomicLong();
   
   private Map<Long,AnnotatedType<?>> types = new ConcurrentHashMap<Long, AnnotatedType<?>>();
  
   public void processInjectionTarget(@Observes ProcessInjectionTarget<?> event)
   {
      if(event.getAnnotatedType().getBaseType() instanceof IdentifiableBean)
      {
         long id = currentId.incrementAndGet();
         types.put(id, event.getAnnotatedType());
         event.setInjectionTarget(new IdentifiableInjectionTarget(event.getInjectionTarget(), id));
      }
   }
   
   public AnnotatedType<?> getAnnotatedType(long id)
   {
      return types.get(id);
   }
   
}
