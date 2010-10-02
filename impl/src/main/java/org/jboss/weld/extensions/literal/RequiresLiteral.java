package org.jboss.weld.extensions.literal;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.weld.extensions.core.Requires;

public class RequiresLiteral extends AnnotationLiteral<Requires> implements Requires
{

   private static final long serialVersionUID = 4907169607105615674L;
   
   private final String[] classes;

   public RequiresLiteral(String[] classes)
   {
      this.classes = classes;
   }

   public String[] value()
   {
      return classes;
   }
}