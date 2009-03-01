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
package org.jboss.webbeans.lifecycle;

import org.jboss.webbeans.CurrentManager;
import org.jboss.webbeans.ManagerImpl;
import org.jboss.webbeans.context.ApplicationContext;
import org.jboss.webbeans.context.DependentContext;
import org.jboss.webbeans.context.api.BeanStore;
import org.jboss.webbeans.environment.se.events.Shutdown;
import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logging;

/**
 * Implements the lifecycle methods for a WebBeans SE application.
 * @author Peter Royle
 */
public class ApplicationLifecycle
{

    private static ApplicationLifecycle instance;
    Log log = Logging.getLog( ApplicationLifecycle.class );


    static
    {
        instance = new ApplicationLifecycle();
    }

    public static ApplicationLifecycle instance()
    {
        return instance;
    }

    public void initialize()
    {
        ManagerImpl manager = CurrentManager.rootManager();
        if (manager == null)
        {
            throw new IllegalStateException( "Manager has not been initialized, " +
                    "check that Bootstrap.initialize() has run" );
        }
        manager.addContext( DependentContext.create() );
        manager.addContext( ApplicationContext.create() );
    }

    public void beginApplication( String id, BeanStore applicationBeanStore )
    {
        log.trace( "Starting application " + id );
        ApplicationContext.INSTANCE.setBeanStore( applicationBeanStore );
        ApplicationContext.INSTANCE.setActive( true );
        // TODO (PR): I have no idea if this is OK??? The WB spec says that the
        // dependant scoe is only active when the manager is doing its work.
        // This leaves it open for the whole duration. I guess that's about
        // right since the main bean is in the dependant scope?
        DependentContext.INSTANCE.setActive( true );
    }

    public void endApplication( String id, BeanStore applicationBeanStore )
    {
        log.trace( "Ending application " + id );

        // Give apps and modules a chance to shutdown cleanly
        CurrentManager.rootManager().fireEvent( new Shutdown() );

        ApplicationContext.INSTANCE.destroy();
        ApplicationContext.INSTANCE.setActive( false );
        ApplicationContext.INSTANCE.setBeanStore( null );

    }

}
