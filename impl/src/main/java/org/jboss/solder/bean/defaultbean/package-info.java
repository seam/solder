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
 * Allows a library to provide a default implmentation of a bean, which is used unless overridden by an application.
 * Although this may sound identical to an alternative, alternatives have some restrictions that may make them
 * undesirable. Primarily, alternatives require an entry in every <code>beans.xml</code> file in an application.
 * </p>
 *
 * <p>
 * Developers consuming the extension will have to open up the any jar file which references the default bean, and edit
 * the <code>beans.xml</code> file within, in order to override the service. This is where default beans come in.
 * </p>
 *
 * <p>
 * Default beans allow you to create a default bean with a specified type and set of qualifiers. If no other bean is
 * installed that has the same type and qualifiers, then the default bean will be installed.
 * </p>
 *
 * @see org.jboss.solder.bean.defaultbean.DefaultBean
 */
package org.jboss.solder.bean.defaultbean;

