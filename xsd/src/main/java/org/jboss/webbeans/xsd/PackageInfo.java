package org.jboss.webbeans.xsd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Document;
import org.jboss.webbeans.xsd.model.TypedModel;

public class PackageInfo
{
   private List<String> namespaces;
   private Document schema;
   private String packageName;
   private Map<String, Set<String>> typeReferences;
   private String namespace;

   public PackageInfo(String packageName)
   {
      this.packageName = packageName;
      typeReferences = new HashMap<String, Set<String>>();
   }

   public void addTypeReferences(Set<TypedModel> references)
   {
      for (TypedModel reference : references)
      {
         Set<String> typeNames = typeReferences.get(reference.getTypePackage());
         if (typeNames == null)
         {
            typeNames = new HashSet<String>();
            typeReferences.put(reference.getTypePackage(), typeNames);
         }
         typeNames.add(reference.getType());
      }
   }

   public List<String> getNamespaces()
   {
      return namespaces;
   }

   public void setNamespaces(List<String> namespaces)
   {
      this.namespaces = namespaces;
   }

   public Document getSchema()
   {
      return schema;
   }

   public void setSchema(Document schema)
   {
      this.schema = schema;
   }

   public String getPackageName()
   {
      return packageName;
   }

   public void setPackageName(String packageName)
   {
      this.packageName = packageName;
   }

   public Map<String, Set<String>> getTypeReferences()
   {
      return typeReferences;
   }

   public String getNamespace()
   {
      return namespace;
   }

   public void setNamespace(String namespace)
   {
      this.namespace = namespace;
   }
}
