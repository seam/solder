package org.jboss.weld.extensions.autoproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.jboss.weld.extensions.bean.BeanBuilder;

public class AutoProxyExtension implements Extension
{
   Set<Bean<?>> beans = new HashSet<Bean<?>>();
   
   public <X> void processAnnotatedType(@Observes ProcessAnnotatedType<X> event, BeanManager beanManager)
   {

      for(Class<?> c : event.getAnnotatedType().getJavaClass().getInterfaces())
      {
         if (c.isAnnotationPresent(AutoProxy.class))
         {
            AutoProxy an = c.getAnnotation(AutoProxy.class);
            Class<? extends InvocationHandler> handlerClass = an.value();
            Class<? extends X> proxyClass = (Class<? extends X>) Proxy.getProxyClass(event.getAnnotatedType().getJavaClass().getClassLoader(), event.getAnnotatedType().getJavaClass());
            BeanBuilder<X> builder = new BeanBuilder<X>(event.getAnnotatedType(), beanManager).defineBeanFromAnnotatedType();
            InjectionTarget<?> tgt = beanManager.createInjectionTarget(beanManager.createAnnotatedType(handlerClass));
            DelegatingInjectionTarget<X> delegatingTarget = new DelegatingInjectionTarget(tgt);
            builder.setInjectionTarget(delegatingTarget);
            AutoProxyBeanLifecycle<X> life = new AutoProxyBeanLifecycle<X>(proxyClass, handlerClass);
            builder.setBeanLifecycle(life);
            beans.add(builder.create());
         }
      }
   }

   public void afterBeanDiscovery(@Observes AfterBeanDiscovery event)
   {
      for (Bean<?> b : beans)
      {
         event.addBean(b);
      }
   }
}
