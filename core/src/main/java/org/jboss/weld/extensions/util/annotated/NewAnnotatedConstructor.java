package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Constructor;

import javax.enterprise.inject.spi.AnnotatedConstructor;

/**
 * 
 * @author Stuart Douglas
 *
 */
class NewAnnotatedConstructor<X> extends AbstractNewAnnotatedCallable<X, Constructor<X>>
      implements AnnotatedConstructor<X>
{

   NewAnnotatedConstructor(NewAnnotatedType<X> type, Constructor<?> constructor, boolean readAnnotations)
   {
      super(type, (Constructor<X>) constructor, readAnnotations);
   }

}
