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

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;

import org.jboss.weld.extensions.annotated.AnnotatedTypeBuilder;

/**
 * Extension that adds classes from the el package as AnnotatedTypes
 * 
 * @author stuart
 * 
 */
public class ELExtension implements Extension
{
   public void beforeBeanDiscovery(@Observes BeforeBeanDiscovery event)
   {
      event.addAnnotatedType(new AnnotatedTypeBuilder<ELContextProducer>().readFromType(ELContextProducer.class).create());
      event.addAnnotatedType(new AnnotatedTypeBuilder<ELResolverImpl>().readFromType(ELResolverImpl.class).create());
      event.addAnnotatedType(new AnnotatedTypeBuilder<ExpressionFactoryProducer>().readFromType(ExpressionFactoryProducer.class).create());
      event.addAnnotatedType(new AnnotatedTypeBuilder<Expressions>().readFromType(Expressions.class).create());
      event.addAnnotatedType(new AnnotatedTypeBuilder<FunctionMapperImpl>().readFromType(FunctionMapperImpl.class).create());
      event.addAnnotatedType(new AnnotatedTypeBuilder<VariableMapperImpl>().readFromType(VariableMapperImpl.class).create());
      event.addQualifier(Mapper.class);
      event.addQualifier(Resolver.class);
   }
}