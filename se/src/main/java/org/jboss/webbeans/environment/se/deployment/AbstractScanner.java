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

import org.jboss.webbeans.environment.se.discovery.WebBeanDiscoveryException;
import org.jboss.webbeans.log.LogProvider;
import org.jboss.webbeans.log.Logging;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javassist.bytecode.ClassFile;

/**
 * Abstract base class for {@link Scanner} providing common functionality
 *
 * This class provides file-system orientated scanning
 *
 * @author Pete Muir
 *
 */
public abstract class AbstractScanner
    implements Scanner
{
    private static class Handler
    {
        private ClassDescriptor classDescriptor;
        private Set<FileDescriptor> fileDescriptors;
        private Set<DeploymentHandler> deploymentHandlers;
        private ClassLoader classLoader;
        private String name;

        public Handler( String name, Set<DeploymentHandler> deploymentHandlers, ClassLoader classLoader )
        {
            this.deploymentHandlers = deploymentHandlers;
            this.name = name;
            this.classLoader = classLoader;
        }

        /**
         * Return true if the file was handled (false if it was ignored)
         */
        protected boolean handle( DeploymentHandler deploymentHandler )
        {
            boolean handled = false;

            if ( deploymentHandler instanceof ClassDeploymentHandler )
            {
                if ( name.endsWith( ".class" ) )
                {
                    ClassDeploymentHandler classDeploymentHandler = (ClassDeploymentHandler) deploymentHandler;

                    if ( getClassDescriptor(  ).getClazz(  ) != null )
                    {
                        log.trace( "adding class to deployable list " + name + " for deployment handler " +
                                   deploymentHandler.toString() );
                        classDeploymentHandler.getClasses(  ).add( getClassDescriptor(  ) );
                        handled = true;
                    } else
                    {
                        log.info( "skipping class " + name +
                                  " because it cannot be loaded (may reference a type which is not available on the classpath)" );
                    }
                }
            } else
            {
                if ( name.equals ("beans.xml" ) )
                {
                    deploymentHandler.getResources(  ).addAll( getAllFileDescriptors(  ) );
                    handled = true;
                }
            }

            return handled;
        }

        protected boolean handle(  )
        {
            log.trace( "found " + name );

            boolean handled = false;

            for ( DeploymentHandler entry : deploymentHandlers )
            {
                if ( handle( entry ) )
                {
                    handled = true;
                }
            }

            return handled;
        }

        private ClassDescriptor getClassDescriptor(  )
        {
            if ( classDescriptor == null )
            {
                classDescriptor = new ClassDescriptor( name, classLoader );
            }

            return classDescriptor;
        }

        private Set<FileDescriptor> getAllFileDescriptors(  )
        {
            if ( fileDescriptors == null )
            {
                try
                {
                    Enumeration<URL> allUrls = classLoader.getResources( name );
                    Set<FileDescriptor> fileDescSet = new HashSet<FileDescriptor>(  );

                    while ( allUrls.hasMoreElements(  ) )
                    {
                        fileDescSet.add( new FileDescriptor( 
                                                             name,
                                                             allUrls.nextElement(  ) ) );
                    }

                    this.fileDescriptors = fileDescSet;
                } catch ( IOException ex )
                {
                    throw new WebBeanDiscoveryException( "Error loading all classpath urls for file " + name, ex );
                }
            }

            return fileDescriptors;
        }
    }

    private static final LogProvider log = Logging.getLogProvider( Scanner.class );
    private DeploymentStrategy deploymentStrategy;

    public AbstractScanner( DeploymentStrategy deploymentStrategy )
    {
        this.deploymentStrategy = deploymentStrategy;
        ClassFile.class.getPackage(  ); //to force loading of javassist, throwing an exception if it is missing
    }

    public DeploymentStrategy getDeploymentStrategy(  )
    {
        return deploymentStrategy;
    }

    protected boolean handle( String name )
    {
        return new Handler( name,
                            deploymentStrategy.getDeploymentHandlers(  ),
                            deploymentStrategy.getClassLoader(  ) ).handle(  );
    }

}
