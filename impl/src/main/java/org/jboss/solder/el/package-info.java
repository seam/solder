/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * <p>
 * Provides an application wide EL value and method expression resolution facility as well as a improved API for
 * evaluating EL expressions aimed at ease of use.
 * </p>
 *
 * <p>
 * To use the improved API, inject the Expressions bean, and call one of it's <code>evaluate</code> methods:
 * </p>
 *
 * <pre>
 *    &#64;Inject Expressions expressions;
 *
 *    ...
 *
 *    Address address = expressions.evaluateValueExpression("#{person.address}");
 *
 *    ...
 *
 *    expressions.evaluateMethodExpression("#{userManager.savePerson}");
 * </pre>
 *
 * <p>
 * By default Solder will only resolve beans from CDI, and provides no function or variable mapping.
 * </p>
 *
 * <p>
 * If you integrating Solder into an environment that provides a source of beans for EL resolution, you can
 * register an {@link javax.el.ELResolver} by creating a bean of type {@link javax.el.ELResolver} with the qualifier
 * &#64;{@link org.jboss.solder.el.Resolver}.
 * </p>
 *
 * <p>
 * If you integrating Solder into an environment that provides a function or variable mapper, you can also
 * provide an alternative {@link javax.el.FunctionMapper} or {@link javax.el.VariableMapper}. Simply create a bean
 * exposing your alternative implementation with the qualifier &#64;{@link org.jboss.solder.el.Mapper}.
 * </p>
 *
 * @see org.jboss.solder.el.Expressions
 * @see org.jboss.solder.el.Resolver
 * @see org.jboss.solder.el.Mapper
 */
package org.jboss.solder.el;

