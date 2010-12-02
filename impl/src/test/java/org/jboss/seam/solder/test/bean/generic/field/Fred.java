package org.jboss.seam.solder.test.bean.generic.field;

public class Fred
{
   private final String value;

   // need to make fred proxiable
   public Fred()
   {
      value = null;
   }

   public Fred(String value)
   {
      this.value = value;
   }

   public String getValue()
   {
      return value;
   }

}
