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
package org.jboss.webbeans.environment.se.deployment;

import java.util.HashSet;
import java.util.Set;

/**
 * Abstract base class for {@link DeploymentHandler} providing common functionality
 *
 * @author Pete Muir
 *
 */
public abstract class AbstractDeploymentHandler
    implements DeploymentHandler
{
    private Set<FileDescriptor> resources;

    public AbstractDeploymentHandler(  )
    {
        resources = new HashSet<FileDescriptor>(  );
    }

    @Override
    public String toString(  )
    {
        return getName(  );
    }

    public void setResources( Set<FileDescriptor> resources )
    {
        this.resources = resources;
    }

    public Set<FileDescriptor> getResources(  )
    {
        return resources;
    }

    public void postProcess( ClassLoader classLoader )
    {
    }
}
