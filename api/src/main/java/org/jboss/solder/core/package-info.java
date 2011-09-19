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
 * A number enhancements to the CDI programming model which are under trial and may be included in later releases
 * of <em>Contexts and Dependency Injection</em>.
 * </p>
 *
 * <p>
 * Included are:
 *
 * <table>
 *    <tr>
 *       <td><code>&#64;{@link org.jboss.solder.core.Veto}</code></td>
 *       <td>Prevents a class from being installed as a bean</td>
 *    </tr>
 *    <tr>
 *       <td><code>&#64;{@link org.jboss.solder.core.Requires}</code></td>
 *       <td>Prevents a class from being installed as a bean unless class dependencies are satisfied</td>
 *    </tr>
 *    <tr>
 *       <td><code>&#64;{@link org.jboss.solder.core.Exact}</code></td>
 *       <td>Specify an implementation of an injection point type</td>
 *    </tr>
 *    <tr>
 *       <td><code>&#64;{@link org.jboss.solder.core.Client}</code></td>
 *       <td>Qualifier identifying a bean as belonging to the current client</td>
 *    </tr>
 *    <tr>
 *       <td></td>
 *       <td>Named packages</td>
 *    </tr>
 * </table>
 *
 * @see org.jboss.solder.core.Veto
 * @see org.jboss.solder.core.Requires
 * @see org.jboss.solder.core.Exact
 * @see org.jboss.solder.core.Client
 * @see javax.inject.Named
 */
package org.jboss.solder.core;

