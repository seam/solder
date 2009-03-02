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
package org.jboss.webbeans.environment.se.discovery;

import java.io.File;
import org.apache.log4j.Logger;

import org.jboss.webbeans.bootstrap.spi.WebBeanDiscovery;
import org.jboss.webbeans.ejb.spi.EjbDescriptor;
import org.jboss.webbeans.environment.se.deployment.ClassDescriptor;
import org.jboss.webbeans.environment.se.deployment.FileDescriptor;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import org.jboss.webbeans.environment.se.deployment.DeploymentHandler;
import org.jboss.webbeans.environment.se.deployment.SimpleWebBeansDeploymentHandler;
import org.jboss.webbeans.environment.se.deployment.URLScanner;
import org.jboss.webbeans.environment.se.deployment.WebBeansXmlDeploymentHandler;

/**
 * The means by which Web Beans are discovered on the classpath. This will only
 * discover simple web beans - there is no EJB/Servlet/JPA integration.
 * @author Peter Royle.
 * Adapted from org.jboss.webbeans.integration.jbossas.WebBeansDiscoveryImpl (author unknown)
 */
public class WebBeanDiscoveryImpl
    implements WebBeanDiscovery
{
    private Set<Class<?>> wbClasses = new HashSet<Class<?>>(  );
    private Set<URL> wbUrls = new HashSet<URL>(  );
//    private StandardDeploymentStrategy deploymentStrategy;
    private SimpleWebBeansDeploymentHandler simpleWebBeansDeploymentHandler = new SimpleWebBeansDeploymentHandler();
    private WebBeansXmlDeploymentHandler webBeansXmlDeploymentHandler = new WebBeansXmlDeploymentHandler();
    URLScanner urlScanner;


    // The log provider
    private static Logger log = Logger.getLogger( WebBeanDiscoveryImpl.class.getName(  ) );

    public WebBeanDiscoveryImpl(  )
    {
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Set<DeploymentHandler> deploymentHandlers = new HashSet<DeploymentHandler>();
        deploymentHandlers.add( simpleWebBeansDeploymentHandler );
        deploymentHandlers.add( webBeansXmlDeploymentHandler );

        urlScanner = new URLScanner( deploymentHandlers, contextClassLoader );
        urlScanner.scanResources( new String[] { "beans.xml"} );
        urlScanner.scanDirectories( new File[]{} );

        findWebBeansXmlUrls(  );
        findWebBeansAnnotatedClasses(  );
    }

    public Iterable<EjbDescriptor<?>> discoverEjbs(  )
    {
        return new HashSet<EjbDescriptor<?>>(  );
    }

    public Iterable<Class<?>> discoverWebBeanClasses(  )
    {
        return wbClasses;
    }

    public Iterable<URL> discoverWebBeansXml(  )
    {
        return wbUrls;
    }

    private void findWebBeansAnnotatedClasses(  )
                                       throws WebBeanDiscoveryException
    {
        for ( ClassDescriptor classDesc : simpleWebBeansDeploymentHandler.getClasses() )
        {
            final Class<?> clazz = classDesc.getClazz(  );
            wbClasses.add( clazz );
        }
    }

    private void findWebBeansXmlUrls(  )
    {
        for ( FileDescriptor fileDesc : webBeansXmlDeploymentHandler.getResources() )
        {
            wbUrls.add( fileDesc.getUrl(  ) );
        }
    }
}
