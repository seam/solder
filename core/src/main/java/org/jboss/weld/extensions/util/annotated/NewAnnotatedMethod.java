package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Method;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * 
 * @author Stuart Douglas
 * 
 */
class NewAnnotatedMethod<X> extends AbstractNewAnnotatedCallable<X, Method> implements AnnotatedMethod<X>
{
   NewAnnotatedMethod(AnnotatedType<X> type, Method method, boolean readAnnotations)
   {
      super(type, method, readAnnotations);
      int count = 0;
      for (Class<?> c : method.getParameterTypes())
      {
         NewAnnotatedParameter<X> mp = new NewAnnotatedParameter<X>(this, c, count++, readAnnotations);
         getParameters().add(mp);
      }
   }
}
