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

import org.jboss.webbeans.log.LogProvider;
import org.jboss.webbeans.log.Logging;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A {@link DeploymentStrategy} coordinates the deployment of resources for a
 * Web Beans SE application.
 *
 * @author Pete Muir
 *
 */
public abstract class DeploymentStrategy
{
    private static final LogProvider log = Logging.getLogProvider( DeploymentStrategy.class );
    private Scanner scanner;
    private List<File> files = new ArrayList<File>(  );
    private Map<String, DeploymentHandler> deploymentHandlers;

    /**
     * The key under which to list possible scanners. System properties take
     * precedence over /META-INF/seam-scanner.properties. Entries will be tried
     * in sequential order until a Scanner can be loaded.
     *
     * This can be specified as a System property or in
     * /META-INF/seam-deployment.properties
     */
    public static final String SCANNERS_KEY = "org.jboss.webbeans.environment.se.deployment.scanners";

    /**
     * Do the scan for resources
     *
     * Should only be called by Seam
     *
     */
    public abstract void scan(  );

    /**
     * Get the scanner being used
     *
     */
    protected Scanner getScanner(  )
    {
        if ( scanner == null )
        {
            initScanner(  );
        }

        return scanner;
    }

    /**
     * Get the classloader to use
     */
    public abstract ClassLoader getClassLoader(  );

    /**
     * Get (or modify) any registered {@link DeploymentHandler}s
     *
     * Implementations of {@link DeploymentStrategy} may add default
     * {@link DeploymentHandler}s
     */
    public Map<String, DeploymentHandler> getDeploymentHandlers(  )
    {
        if ( deploymentHandlers == null )
        {
            initDeploymentHandlers(  );
        }

        return this.deploymentHandlers;
    }

    private void initDeploymentHandlers(  )
    {
        this.deploymentHandlers = new HashMap<String, DeploymentHandler>(  );

        List<String> deploymentHandlersClassNames =
            new WebBeansDeploymentProperties( getClassLoader(  ) ).getPropertyValues( getDeploymentHandlersKey(  ) );
        addHandlers( deploymentHandlersClassNames );
    }

    protected abstract String getDeploymentHandlersKey(  );

    protected void initScanner(  )
    {
        List<String> scanners =
            new WebBeansDeploymentProperties( getClassLoader(  ) ).getPropertyValues( SCANNERS_KEY );

        for ( String className : scanners )
        {
            Scanner scanner = instantiateScanner( className );

            if ( scanner != null )
            {
                log.debug( "Using " + scanner.toString(  ) );
                this.scanner = scanner;

                return;
            }
        }

        log.debug( "Using default URLScanner" );
        this.scanner = new URLScanner( this );
    }

    private Scanner instantiateScanner( String className )
    {
        try
        {
            Class<Scanner> scannerClass = (Class<Scanner>) getClassLoader(  ).loadClass( className );
            Constructor<Scanner> constructor = scannerClass.getConstructor( new Class[] { DeploymentStrategy.class } );

            return constructor.newInstance( new Object[] { this } );
        } catch ( ClassNotFoundException e )
        {
            log.trace( "Unable to use " + className + " as scanner (class not found)", e );
        } catch ( NoClassDefFoundError e )
        {
            log.trace( "Unable to use " + className + " as scanner (dependency not found)", e );
        } catch ( ClassCastException e )
        {
            log.trace( "Unable to use " + className +
                       " as scanner (class does not implement org.jboss.seam.deployment.Scanner)" );
        } catch ( InstantiationException e )
        {
            log.trace( "Unable to instantiate scanner " + className, e );
        } catch ( IllegalAccessException e )
        {
            log.trace( "Unable to instantiate scanner " + className, e );
        } catch ( SecurityException e )
        {
            log.trace( className + " must declare public " + className +
                       "( ClassLoader classLoader, String ... resourceNames )", e );
        } catch ( NoSuchMethodException e )
        {
            log.trace( className + " must declare public " + className +
                       "( ClassLoader classLoader, String ... resourceNames )", e );
        } catch ( IllegalArgumentException e )
        {
            log.trace( className + " must declare public " + className +
                       "( ClassLoader classLoader, String ... resourceNames )", e );
        } catch ( InvocationTargetException e )
        {
            log.trace( className + " must declare public " + className +
                       "( ClassLoader classLoader, String ... resourceNames )", e );
        }

        return null;
    }

    private void addHandlers( List<String> handlers )
    {
        for ( String handler : handlers )
        {
            addHandler( handler );
        }
    }

    private void addHandler( String className )
    {
        DeploymentHandler deploymentHandler = instantiateDeploymentHandler( className );

        if ( deploymentHandler != null )
        {
            log.debug( "Adding " + deploymentHandler + " as a deployment handler" );
            deploymentHandlers.put( deploymentHandler.getName(  ),
                                    deploymentHandler );
        }
    }

    private DeploymentHandler instantiateDeploymentHandler( String className )
    {
        try
        {
            Class<DeploymentHandler> clazz = (Class<DeploymentHandler>) getClassLoader(  ).loadClass( className );

            return clazz.newInstance(  );
        } catch ( ClassNotFoundException e )
        {
            log.trace( "Unable to use " + className + " as a deployment handler (class not found)", e );
        } catch ( NoClassDefFoundError e )
        {
            log.trace( "Unable to use " + className + " as a deployment handler (dependency not found)", e );
        } catch ( InstantiationException e )
        {
            log.trace( "Unable to instantiate deployment handler " + className, e );
        } catch ( IllegalAccessException e )
        {
            log.trace( "Unable to instantiate deployment handler " + className, e );
        }

        return null;
    }

    public List<File> getFiles(  )
    {
        return files;
    }

    public void setFiles( List<File> files )
    {
        this.files = files;
    }

    public long getTimestamp(  )
    {
        return getScanner(  ).getTimestamp(  );
    }

}
