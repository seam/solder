package org.jboss.weld.extensions.literal;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.weld.extensions.core.Exact;

public class ExactLiteral extends AnnotationLiteral<Exact> implements Exact
{

   private static final long serialVersionUID = 4907169607105615674L;
   
   private final Class<?> clazz;

   public ExactLiteral(Class<?> clazz)
   {
      this.clazz = clazz;
   }

   public Class<?> value()
   {
      return clazz;
   }
}