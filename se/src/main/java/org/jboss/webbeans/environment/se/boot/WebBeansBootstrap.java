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

import java.lang.annotation.Annotation;
import java.util.List;
import javax.inject.manager.Manager;
import javax.transaction.Transaction;
import org.jboss.webbeans.BeanValidator;
import org.jboss.webbeans.CurrentManager;
import org.jboss.webbeans.ManagerImpl;
import org.jboss.webbeans.bean.standard.InjectionPointBean;
import org.jboss.webbeans.bean.standard.ManagerBean;
import org.jboss.webbeans.bootstrap.BeanDeployer;
import org.jboss.webbeans.bootstrap.BeansXmlParser;
import org.jboss.webbeans.bootstrap.api.helpers.AbstractBootstrap;
import org.jboss.webbeans.context.api.helpers.ConcurrentHashMapBeanStore;
import org.jboss.webbeans.context.api.BeanStore;
import org.jboss.webbeans.environment.se.beans.ParametersFactory;
import org.jboss.webbeans.environment.se.discovery.WebBeanDiscoveryImpl;
import org.jboss.webbeans.environment.se.resources.DefaultResourceLoader;
import org.jboss.webbeans.environment.se.lifecycle.ApplicationLifecycle;
import org.jboss.webbeans.literal.DeployedLiteral;
import org.jboss.webbeans.literal.InitializedLiteral;
import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logging;

/**
 * Bootstrap class for for WebBeans in SE environment. Provides no JNDI binding.
 * 
 * @author Peter Royle
 */
public class WebBeansBootstrap extends AbstractBootstrap
{

    Log log = Logging.getLog( WebBeansBootstrap.class );
    String[] commandLineArgs;
    private ManagerImpl manager;
    ApplicationLifecycle lifecycle = ApplicationLifecycle.instance();
    final BeanStore appBeanStore = new ConcurrentHashMapBeanStore();

    public WebBeansBootstrap( String[] commandLineArgs )
    {
        setResourceLoader( new DefaultResourceLoader() );
        setEjbResolver( new UnsupportedEjbResolver() );
        setEjbDiscovery( new EmptyEjbDiscovery() );
        setNamingContext( new UnsupportedNaming() );
        setWebBeanDiscovery( new WebBeanDiscoveryImpl() );
        this.commandLineArgs = commandLineArgs;

    }

    @Override
    public void initialize()
    {

        if (getResourceLoader() == null)
        {
            throw new IllegalStateException( "ResourceLoader not set" );
        }
        if (getNamingContext() == null)
        {
            throw new IllegalStateException( "NamingContext is not set" );
        }
        if (getEjbResolver() == null)
        {
            throw new IllegalStateException( "EjbResolver is not set" );
        }
        this.manager = new ManagerImpl( getNamingContext(), getEjbResolver(), getResourceLoader() );
        CurrentManager.setRootManager( manager );
        lifecycle.initialize();

    }

    @Override
    public Manager getManager()
    {
        return manager;
    }

    @Override
    public void boot()
    {
        synchronized (this)
        {
            log.info( "Starting Web Beans RI (SE environment) " );
            if (manager == null)
            {
                throw new IllegalStateException( "Manager has not been initialized" );
            }
            if (getWebBeanDiscovery() == null)
            {
                throw new IllegalStateException( "WebBeanDiscovery not set" );
            }
            if (getEjbDiscovery() == null)
            {
                throw new IllegalStateException( "EjbDiscovery is not set" );
            }
            if (getResourceLoader() == null)
            {
                throw new IllegalStateException( "ResourceLoader not set" );
            }

            lifecycle.beginApplication( "TODO: application id?", appBeanStore );

            BeansXmlParser parser = new BeansXmlParser( getResourceLoader(), getWebBeanDiscovery().discoverWebBeansXml() );
            parser.parse();
            List<Class<? extends Annotation>> enabledDeploymentTypes = parser.getEnabledDeploymentTypes();
            if (enabledDeploymentTypes != null)
            {
                manager.setEnabledDeploymentTypes( enabledDeploymentTypes );
            }
            registerBeans( getWebBeanDiscovery().discoverWebBeanClasses() );

            // grab the ParametersFactory and inject the given commandline arguments
            ParametersFactory paramsFactory =
                    (ParametersFactory) CurrentManager.rootManager().getInstanceByType( ParametersFactory.class );
            paramsFactory.setArgs( this.commandLineArgs );

            manager.fireEvent( manager, new InitializedLiteral() );
            log.info( "Web Beans initialized. Validating beans." );
            manager.getResolver().resolveInjectionPoints();
            new BeanValidator( manager ).validate();
            manager.fireEvent( manager, new DeployedLiteral() );
        }
    }

    /**
     * Register the bean with the getManager(), including any standard (built in)
     * beans
     *
     * @param classes The classes to register as Web Beans
     */
    protected void registerBeans( Iterable<Class<?>> classes )
    {
        BeanDeployer beanDeployer = new BeanDeployer( manager );
        beanDeployer.addClasses( classes );
        beanDeployer.addBean( ManagerBean.of( manager ) );
        beanDeployer.addBean( InjectionPointBean.of( manager ) );
        beanDeployer.addClass( Transaction.class );
        beanDeployer.deploy();
    }

   public void shutdown()
   {
        lifecycle.endApplication( "TODO: application id?", appBeanStore );
   }

}
