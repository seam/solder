package org.jboss.webbeans.xsd.model;

import org.dom4j.Element;
import org.jboss.webbeans.xsd.NamespaceHandler;

public class FieldModel extends NamedModel
{

   protected FieldModel(String name)
   {
      super(name);
   }

   public static FieldModel of(String name)
   {
      return new FieldModel(name);
   }

   @Override
   public Element toXSD(NamespaceHandler namespaceHandler)
   {
      Element field = super.toXSD(namespaceHandler);
      field.addAttribute("type", "wb:field");    
      return field;
   }

}
