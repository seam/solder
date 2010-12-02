package org.jboss.seam.solder.test.el;

import java.beans.FeatureDescriptor;
import java.util.Iterator;

import javax.el.ELContext;
import javax.el.ELResolver;

import org.jboss.seam.solder.el.Resolver;

@Resolver
public class CustomELResolver extends ELResolver
{

   @Override
   public Object getValue(ELContext context, Object base, Object property)
   {
      if ("foo".equals(property))
      {
         context.setPropertyResolved(true);
         return "baz";
      }
      else
      {
         return null;
      }
   }

   @Override
   public Class<?> getType(ELContext context, Object base, Object property)
   {
      if ("foo".equals(property))
      {
         return String.class;
      }
      else
      {
         return null;
      }
   }

   @Override
   public void setValue(ELContext context, Object base, Object property, Object value)
   {

   }

   @Override
   public boolean isReadOnly(ELContext context, Object base, Object property)
   {
      return false;
   }

   @Override
   public Iterator<FeatureDescriptor> getFeatureDescriptors(ELContext context, Object base)
   {
      return null;
   }

   @Override
   public Class<?> getCommonPropertyType(ELContext context, Object base)
   {
      return null;
   }

}
