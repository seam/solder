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
package org.jboss.seam.solder.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.PassivationCapable;

/**
 * <p>
 * A passivation capable narrowing bean allows you to build a general purpose,
 * passivation capable, bean (likely a producer method), and register it for a
 * narrowed type (or qualifiers). For example, you could create a producer
 * method which uses an a String ID to located an object (the object can have
 * any class):
 * </p>
 * 
 * <pre>
 * &#064;Produces
 * // Use some synthetic scope to prevent this from interfering with other
 * // resolutions
 * &#064;MyProducer
 * Object produce(InjectionPoint ip)
 * {
 *    String id = ip.getAnnotated().getAnnotation(Id.class).value();
 *    // Lookup and return the object for the id
 * }
 * </pre>
 * 
 * <p>
 * The delegate bean <em>must</em> return an object which can be cast to the
 * type <code>T</code>, otherwise a {@link ClassCastException} will be thrown at
 * runtime when the bean is created.
 * </p>
 * 
 * <p>
 * You can then register a narrowing bean for each type you need:
 * </p>
 * 
 * <pre>
 * event.addBean(new NarrowingBeanBuilder&lt;T&gt;(delegateBean).readFromType(type).create());
 * </pre>
 * 
 * <p>
 * {@link ImmutablePassivationCapableNarrowingBean} will use the annotations on
 * <code>defininingType</code> to discover the qualifiers, types, scope,
 * stereotypes of the bean, as well as determine it's name (if any) and whether
 * it is an alternative.
 * </p>
 * 
 * <p>
 * The attributes are immutable, and collections are defensively copied on
 * instantiation. It uses the defaults from the specification for properties if
 * not specified.
 * </p>
 * 
 * @author Pete Muir
 * @see NarrowingBeanBuilder
 * @see ImmutableNarrowingBean
 */
public class ImmutablePassivationCapableNarrowingBean<T> extends ImmutableNarrowingBean<T> implements PassivationCapable
{
   private final String id;

   public ImmutablePassivationCapableNarrowingBean(Bean<Object> delegate, String name, Set<Annotation> qualifiers, Class<? extends Annotation> scope, Set<Class<? extends Annotation>> stereotypes, Set<Type> types, boolean alternative, boolean nullable, String toString, String id)
   {
      super(delegate, name, qualifiers, scope, stereotypes, types, alternative, nullable, toString);
      this.id = id;
   }

   public String getId()
   {
      return id;
   }

}
