package org.jboss.seam.solder.literal;

import javax.enterprise.util.AnnotationLiteral;

import org.jboss.seam.solder.bean.defaultbean.DefaultBean;

public class DefaultBeanLiteral extends AnnotationLiteral<DefaultBean> implements DefaultBean
{

   private static final long serialVersionUID = 4907169607105615674L;
   
   private final Class<?> clazz;

   public DefaultBeanLiteral(Class<?> clazz)
   {
      this.clazz = clazz;
   }

   public Class<?> value()
   {
      return clazz;
   }
}