package org.jboss.weld.extensions.literal;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.weld.extensions.bean.generic.GenericType;

public class GenericTypeLiteral extends AnnotationLiteral<GenericType> implements GenericType
{

   private static final long serialVersionUID = 4907169607105615674L;
   
   private final Class<?> clazz;

   public GenericTypeLiteral(Class<?> clazz)
   {
      this.clazz = clazz;
   }

   public Class<?> value()
   {
      return clazz;
   }
}