package org.jboss.webbeans.xsd.model;

import javax.lang.model.element.ExecutableElement;

import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.jboss.webbeans.xsd.NamespaceHandler;

public class ConstructorModel extends MethodModel
{

   protected ConstructorModel(ExecutableElement executableElement)
   {
      super(executableElement);
      name = null;
   }

   public static ConstructorModel of(ExecutableElement executableElement)
   {
      return new ConstructorModel(executableElement);
   }

   @Override
   public boolean equals(Object other)
   {
      ConstructorModel otherModel = (ConstructorModel) other;
      return parameters.equals(otherModel.getParameters());
   }

   @Override
   public int hashCode()
   {
      return parameters.hashCode();
   }

   @Override
   public Element toXSD(NamespaceHandler namespaceHandler)
   {
      Element constructor = DocumentFactory.getInstance().createElement("xs:sequence");
      for (TypedModel parameter : parameters)
      {
         constructor.add(parameter.toXSD(namespaceHandler));
      }
      return constructor;
   }

}
