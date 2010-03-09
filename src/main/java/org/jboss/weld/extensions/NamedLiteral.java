/**
 * 
 */
package org.jboss.weld.extensions;

import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Named;

class NamedLiteral extends AnnotationLiteral<Named> implements Named
{
   private static final long serialVersionUID = 2239690880420187044L;
   final String name;

   NamedLiteral(String name)
   {
      this.name = name;
   }

   public String value()
   {
      return name;
   }
}