package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Constructor;
import java.util.Map;

import javax.enterprise.inject.spi.AnnotatedConstructor;

/**
 * 
 * @author Stuart Douglas
 * 
 */
class NewAnnotatedConstructor<X> extends AbstractNewAnnotatedCallable<X, Constructor<X>> implements AnnotatedConstructor<X>
{

   NewAnnotatedConstructor(NewAnnotatedType<X> type, Constructor<?> constructor, AnnotationStore annotations, Map<Integer, AnnotationStore> parameterAnnotations)
   {
      super(type, (Constructor<X>) constructor, constructor.getDeclaringClass(), constructor.getParameterTypes(), annotations, parameterAnnotations, null);
   }

}
