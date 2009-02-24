/**
 * JBoss, Home of Professional Open Source
 * Copyright 2008, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.webbeans.environment.se.boot;

import org.jboss.webbeans.bootstrap.spi.EjbDiscovery;
import org.jboss.webbeans.ejb.spi.EjbDescriptor;

import java.util.Collections;

/**
 *
 * @author JBoss.org, modifications by Peter Royle
 */
public class EmptyEjbDiscovery
        implements EjbDiscovery
{

    public Iterable<EjbDescriptor<?>> discoverEjbs()
    {
        return Collections.EMPTY_SET;
    }

    protected static Iterable<Class<?>> discoverEjbs( Iterable<Class<?>> webBeanClasses )
    {
        return Collections.EMPTY_SET;
    }
}
