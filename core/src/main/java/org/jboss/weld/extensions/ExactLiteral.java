/**
 * 
 */
package org.jboss.weld.extensions;

import javax.enterprise.util.AnnotationLiteral;

class ExactLiteral extends AnnotationLiteral<Exact> implements Exact
{

   private static final long serialVersionUID = 4907169607105615674L;
   
   final Class<?> clazz;

   ExactLiteral(Class<?> clazz)
   {
      this.clazz = clazz;
   }

   public Class<?> value()
   {
      return clazz;
   }
}