package org.jboss.webbeans.xsd.model;

public class TypedModel
{
   protected String type;
   protected boolean primitive;

   public String getType()
   {
      return type;
   }

   public void setType(String type)
   {
      this.type = type;
   }

   public boolean isPrimitive()
   {
      return primitive;
   }

   public void setPrimitive(boolean primitive)
   {
      this.primitive = primitive;
   }

   public String getTypePackage()
   {
      if (primitive)
      {
         return "";
      }
      int lastDot = type.lastIndexOf(".");
      return lastDot < 0 ? "nopak" : type.substring(0, lastDot);
   }

   public String getTypeSimpleName()
   {
      int lastDot = type.lastIndexOf(".");
      return lastDot < 0 ? type : type.substring(lastDot + 1);
   }

   @Override
   public boolean equals(Object other)
   {
      return type.equals(other);
   }

   @Override
   public int hashCode()
   {
      return type.hashCode();
   }

}
