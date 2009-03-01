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

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * The standard deployment strategy used with Seam, deploys non-hot-deployable
 * Seam components and namespaces
 *
 * @author Pete Muir
 *
 */
public class StandardDeploymentStrategy
    extends DeploymentStrategy
{
    private ClassLoader classLoader;

    /**
     * The files used to identify a Seam archive
     */
    public static final String[] RESOURCE_NAMES = { "beans.xml" };

    private SimpleWebBeansDeploymentHandler simpleWebBeansDeploymentHandler;
    private WebBeansXmlDeploymentHandler webBeansXmlDeploymentHandler;

    /**
     * @param classLoader The classloader used to load and handle resources
     */
    public StandardDeploymentStrategy( ClassLoader classLoader )
    {
        this.classLoader = Thread.currentThread(  ).getContextClassLoader(  );
        simpleWebBeansDeploymentHandler = new SimpleWebBeansDeploymentHandler(  );
        getDeploymentHandlers(  ).add( simpleWebBeansDeploymentHandler );
        webBeansXmlDeploymentHandler = new WebBeansXmlDeploymentHandler(  );
        getDeploymentHandlers(  ).add( webBeansXmlDeploymentHandler );
    }

    @Override
    public ClassLoader getClassLoader(  )
    {
        return classLoader;
    }

    /**
     * Get all annotated components known to this strategy
     */
    public Set<ClassDescriptor> getSimpleWebBeans(  )
    {
        return Collections.unmodifiableSet( simpleWebBeansDeploymentHandler.getClasses(  ) );
    }

    /**
     * Get all beans.xml locations
     */
    public Set<FileDescriptor> getWebBeansXMLs(  )
    {
        Set<FileDescriptor> fileDescriptors = new HashSet<FileDescriptor>(  );
        fileDescriptors.addAll( webBeansXmlDeploymentHandler.getResources(  ) );

        return Collections.unmodifiableSet( fileDescriptors );
    }

    @Override
    public void scan(  )
    {
        getScanner(  ).scanResources( RESOURCE_NAMES );
        getScanner(  ).scanDirectories( getFiles(  ).toArray( new File[0] ) );
    }

}
