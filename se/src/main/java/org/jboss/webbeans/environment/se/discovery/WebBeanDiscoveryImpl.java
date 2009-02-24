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

import org.apache.log4j.Logger;

import org.jboss.webbeans.bootstrap.spi.WebBeanDiscovery;
import org.jboss.webbeans.ejb.spi.EjbDescriptor;
import org.jboss.webbeans.environment.se.deployment.ClassDescriptor;
import org.jboss.webbeans.environment.se.deployment.FileDescriptor;
import org.jboss.webbeans.environment.se.deployment.StandardDeploymentStrategy;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

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
    private StandardDeploymentStrategy deploymentStrategy;

    // The log provider
    private static Logger log = Logger.getLogger( WebBeanDiscoveryImpl.class.getName(  ) );

    public WebBeanDiscoveryImpl(  )
    {
        deploymentStrategy = new StandardDeploymentStrategy( Thread.currentThread(  ).getContextClassLoader(  ) );
        deploymentStrategy.scan(  );

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
        for ( ClassDescriptor classDesc : deploymentStrategy.getSimpleWebBeans(  ) )
        {
            final Class<?> clazz = classDesc.getClazz(  );
            wbClasses.add( clazz );
        }
    }

    private void findWebBeansXmlUrls(  )
    {
        for ( FileDescriptor fileDesc : deploymentStrategy.getWebBeansXMLs(  ) )
        {
            wbUrls.add( fileDesc.getUrl(  ) );
        }
    }
}
