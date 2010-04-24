package org.jboss.weld.extensions.beanlookup;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.inject.Inject;

/**
 * Class that can lookup an AnnotatedType from an instance of a bean
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 *
 */
public class AnnotatedTypeIdentifier
{
   @Inject IdentifiableBeanExtension beans;
   
   public AnnotatedType<?> getAnnotatedType(Object instance)
   {
      return beans.getAnnotatedType(instance);
   }
}
