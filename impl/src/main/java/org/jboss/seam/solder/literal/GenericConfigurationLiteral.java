package org.jboss.seam.solder.literal;

import java.lang.annotation.Annotation;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.seam.solder.bean.generic.GenericConfiguration;

public class GenericConfigurationLiteral extends AnnotationLiteral<GenericConfiguration> implements GenericConfiguration
{

   private static final long serialVersionUID = -1931707390692943775L;

   private final Class<? extends Annotation> value;
   
   public GenericConfigurationLiteral(Class<? extends Annotation> value)
   {
      this.value = value;
   }

   public Class<? extends Annotation> value()
   {
      return value;
   }

}