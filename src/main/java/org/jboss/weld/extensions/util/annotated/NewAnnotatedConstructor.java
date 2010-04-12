package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Map;

import javax.enterprise.inject.spi.AnnotatedConstructor;

/**
 * 
 * @author Stuart Douglas
 * 
 */
class NewAnnotatedConstructor<X> extends AbstractNewAnnotatedCallable<X, Constructor<X>> implements AnnotatedConstructor<X>
{

   NewAnnotatedConstructor(NewAnnotatedType<X> type, Constructor<?> constructor, AnnotationStore annotations, Map<Integer, AnnotationStore> parameterAnnotations, Map<Integer, Type> typeOverrides)
   {
      super(type, (Constructor<X>) constructor, constructor.getDeclaringClass(), constructor.getParameterTypes(), constructor.getGenericParameterTypes(), annotations, parameterAnnotations, null, typeOverrides);
   }

}
