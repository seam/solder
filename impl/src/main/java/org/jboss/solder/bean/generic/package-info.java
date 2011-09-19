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
 * Allows a library to expose sets of beans which may be configured multiple times in an application. When exposing
 * these services via CDI, it would be time consuming and error prone to force the end developer to provide producers
 * for all the different classes required. Generic beans provides a solution, allowing a framework author to
 * provide a set of related beans, one for each single configuration point defined by the end developer.
 * The configuration points specifies the qualifiers which are inherited by all beans in the set.
 * </p>
 *
 * @see org.jboss.solder.bean.generic.Generic
 * @see org.jboss.solder.bean.generic.GenericConfiguration
 * @see org.jboss.solder.bean.generic.GenericType
 *
 * @author Pete Muir
 * @author Stuart Douglas
 */

package org.jboss.solder.bean.generic;

