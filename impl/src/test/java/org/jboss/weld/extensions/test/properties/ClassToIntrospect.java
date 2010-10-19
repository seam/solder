package org.jboss.weld.extensions.test.properties;

import java.net.URL;

public class ClassToIntrospect
{
   private String name;

   private String p;
   
   private URL URL;
   
   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }
   
   public String getP()
   {
      return p;
   }
   
   public void setP(String p)
   {
      this.p = p;
   }
   
   public String getTitle()
   {
      return "Hero";
   }
   
   public String get()
   {
      return null;
   }
   
   public boolean is()
   {
      return false;
   }
   
   public void getFooBar()
   {
   }
   
   public void setSalary(Double base, Double bonus)
   {
   }

   public URL getURL()
   {
      return URL;
   }

   public void setURL(URL URL)
   {
      this.URL = URL;
   }
   
   public Boolean isValid()
   {
      return false;
   }
}
