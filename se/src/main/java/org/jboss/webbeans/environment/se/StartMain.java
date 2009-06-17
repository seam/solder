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
package org.jboss.webbeans.environment.se;

import javax.enterprise.inject.spi.BeanManager;

import org.jboss.webbeans.bootstrap.api.Bootstrap;
import org.jboss.webbeans.bootstrap.api.Environments;
import org.jboss.webbeans.bootstrap.spi.WebBeanDiscovery;
import org.jboss.webbeans.context.api.BeanStore;
import org.jboss.webbeans.context.api.helpers.ConcurrentHashMapBeanStore;
import org.jboss.webbeans.environment.se.discovery.SEWebBeanDiscovery;
import org.jboss.webbeans.environment.se.util.Reflections;
import org.jboss.webbeans.environment.se.util.WebBeansManagerUtils;
import org.jboss.webbeans.manager.api.WebBeansManager;

/**
 * This is the main class that should always be called from the command line for
 * a WebBEans SE app. Something like: <code>
 * java -jar MyApp.jar org.jboss.webbeans.environment.se.StarMain arguments
 * </code>
 * 
 * @author Peter Royle
 * @author Pete Muir
 */
public class StartMain
{

    private static final String BOOTSTRAP_IMPL_CLASS_NAME = "org.jboss.webbeans.bootstrap.WebBeansBootstrap";
    private final Bootstrap bootstrap;
    private final BeanStore applicationBeanStore;
    public static String[] PARAMETERS;
    private WebBeansManager manager;

    public StartMain(String[] commandLineArgs)
    {
        PARAMETERS = commandLineArgs;
        try {
            bootstrap = Reflections.newInstance(BOOTSTRAP_IMPL_CLASS_NAME, Bootstrap.class);
        } catch (Exception e) {
            throw new IllegalStateException("Error loading Web Beans bootstrap, check that Web Beans is on the classpath", e);
        }
        this.applicationBeanStore = new ConcurrentHashMapBeanStore();
    }

    public BeanManager go()
    {
        bootstrap.setEnvironment(Environments.SE);
        bootstrap.getServices().add(WebBeanDiscovery.class, new SEWebBeanDiscovery() {});
        bootstrap.setApplicationContext(applicationBeanStore);
        bootstrap.initialize();
        this.manager = bootstrap.getManager();
        bootstrap.boot();
        WebBeansManagerUtils.getInstanceByType(manager, ShutdownManager.class).
                setBootstrap(bootstrap);
        return this.manager;
    }

    /**
     * The main method called from the command line.
     *
     * @param args
     *           the command line arguments
     */
    public static void main(String[] args)
    {
        new StartMain(args).go();
    }

    public static String[] getParameters()
    {
        // TODO(PR): make immutable
        return PARAMETERS;
    }
    
}
