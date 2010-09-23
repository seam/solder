/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.weld.extensions.test.bean.generic.field;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AnnotatedMember;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

import org.jboss.weld.extensions.bean.generic.GenericProduct;
import org.jboss.weld.extensions.bean.generic.Generic;

/**
 * A generic bean for the config annotation Message
 * 
 * @author pmuir
 *
 */

@Generic(Service.class)
public class Garply
{
   
   @Inject @GenericProduct
   private Waldo waldo;
   
   @Inject @GenericProduct
   private AnnotatedMember<?> annotatedMember;
   
   @Produces @WaldoName
   public String getWaldoName()
   {
      return waldo.getName();
   }
   
   @Produces 
   public HashMap<String, String> getMap()
   {
      return new HashMap<String, String>();
   }
   
   public Waldo getWaldo()
   {
      return waldo;
   }
   
   public AnnotatedMember<?> getAnnotatedMember()
   {
      return annotatedMember;
   }
   
}
