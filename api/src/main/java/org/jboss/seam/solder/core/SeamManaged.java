/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.seam.solder.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.context.Dependent;

/**
 * Denotes a bean that is SeamManaged.  Usage is implementation specific,
 * but in general is used for binding the life cycle of a component to a
 * specific transactional scope.  In Persistence, ties the EntityManager/EntityManagerFactory.
 *
 * Old JavaDocs:
 * 
 * Signifies that a resource producer field or producer method that produces an
 * {@link EntityManagerFactory} should also produce a Seam managed persistence
 * context. For example:
 * 
 * <pre>
 * &#064;SeamManaged
 * &#064;Produces
 * &#064;PersistenceUnit
 * &#064;ConversationScoped
 * &#064;SomeQualifier
 * EntityManagerFactory emf;
 * </pre>
 * <p/>
 * Will create a conversation scoped seam managed persistence context that is
 * conversation scoped with the qualifier @SomeQualifier.
 * <p/>
 * This field still produces the EntityManagerFactory with qualifier
 * &#064;SomeQualifier, however the scope for the producer field is changed to
 * {@link Dependent}, as the specification does not allow resource producer
 * fields to have a scope other than Dependent
 * 
 * @author Stuart Douglas
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD })
@Documented
public @interface SeamManaged
{

}
