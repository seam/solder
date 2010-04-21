package org.jboss.weld.extensions.beanid;

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
   
   public AnnotatedType<?> getAnnotatedType(IdentifiableBean instance)
   {
      long id = instance.getBeanId();
      return beans.getAnnotatedType(id);
   }
}
