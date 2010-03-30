package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Map;

import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * 
 * @author Stuart Douglas
 * 
 */
class NewAnnotatedMethod<X> extends AbstractNewAnnotatedCallable<X, Method> implements AnnotatedMethod<X>
{
   NewAnnotatedMethod(AnnotatedType<X> type, Method method, AnnotationStore annotations, Map<Integer, AnnotationStore> parameterAnnotations, Map<Integer, Type> parameterTypeOverrides)
   {
      super(type, method, method.getReturnType(), method.getParameterTypes(), annotations, parameterAnnotations, method.getGenericReturnType(), parameterTypeOverrides);
   }

}
