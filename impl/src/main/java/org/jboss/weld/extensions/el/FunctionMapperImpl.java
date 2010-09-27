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
package org.jboss.weld.extensions.el;

import java.lang.reflect.Method;

import javax.el.FunctionMapper;

import org.jboss.weld.extensions.bean.defaultbean.DefaultBean;

/**
 * Default function mapper bean. Should be overriden by the faces module or any
 * other module that provides a 'real' FunctionMapper
 * 
 * @author Stuart Douglas
 * 
 */
@DefaultBean(FunctionMapper.class)
@Mapper
public class FunctionMapperImpl extends FunctionMapper
{

   @Override
   public Method resolveFunction(String prefix, String localName)
   {
      return null;
   }

}
