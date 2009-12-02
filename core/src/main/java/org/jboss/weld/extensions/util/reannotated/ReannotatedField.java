package org.jboss.weld.extensions.util.reannotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import javax.enterprise.inject.spi.AnnotatedField;

/**
 * This implementation of {@link AnnotatedField} is not threadsafe and any synchronization must be performed by the client
 * 
 * @author Gavin King
 *
 * @param <X>
 */
public class ReannotatedField<X> extends ReannotatedMember<X> implements AnnotatedField<X>
{

   private final AnnotatedField<X> field;

   ReannotatedField(ReannotatedType<X> declaringType, AnnotatedField<X> field)
   {
      super(declaringType);
      this.field = field;
   }

   @Override
   protected AnnotatedField<X> delegate()
   {
      return field;
   }

   @Override
   public Field getJavaMember()
   {
      return field.getJavaMember();
   }

   @Override
   public boolean isStatic()
   {
      return field.isStatic();
   }

   @Override
   public <Y extends Annotation> void redefineAll(Class<Y> annotationType, AnnotationRedefinition<Y> visitor)
   {
      redefine(annotationType, visitor);
   }

}
