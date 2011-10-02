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
 * A set of utilities for working with JDK reflection and Annotated metadata, including:
 * </p>
 *
 * <table>
 *   <tr>
 *     <td><code>AnnotationInspector</code></td><td>Query an {@link java.lang.reflect.AnnotatedElement} for meta-annotations</td>
 *   </tr>
 *   <tr>
 *     <td><code>AnnotationInstanceProvider</code></td><td>Create annotation instances at runtime</td>
 *   </tr>
 *   <tr>
 *     <td><code>Reflections</code></td><td>Utilities for working with JDK reflection and Annotated metadata</td>
 *   </tr>
 *   <tr>
 *     <td><code>Synthetic</code></td><td>Create synthetic qualifiers for disambiguating injection points</td>
 *   </tr>
 * </table>
 *
 * @author Pete Muir
 * @see org.jboss.solder.reflection.AnnotationInspector
 * @see org.jboss.solder.reflection.AnnotationInstanceProvider
 * @see org.jboss.solder.reflection.Reflections
 * @see org.jboss.solder.reflection.Synthetic
 */
package org.jboss.solder.reflection;
