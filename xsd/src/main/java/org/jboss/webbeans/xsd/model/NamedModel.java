/*
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jboss.webbeans.xsd.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A superclass for named models
 * 
 * @author Nicklas Karlsson
 * 
 */
public class NamedModel
{
   protected String name;
   protected Map<String, Set<String>> annotations = new HashMap<String, Set<String>>();

   public NamedModel()
   {
   }

   public NamedModel(String name, Map<String, Set<String>> annotations)
   {
      this.name = name;
      this.annotations.putAll(annotations);
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public Map<String, Set<String>> getAnnotations()
   {
      return annotations;
   }

   public void setAnnotations(Map<String, Set<String>> annotations)
   {
      this.annotations = annotations;
   }
}
