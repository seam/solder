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

import javax.event.Observes;
import org.jboss.webbeans.environment.se.boot.WebBeansBootstrap;
import org.jboss.webbeans.environment.se.events.Shutdown;
import org.jboss.webbeans.environment.se.events.ShutdownRequest;
import org.jboss.webbeans.log.Log;
import org.jboss.webbeans.log.Logging;

/**
 * This is the main class that should always be called from the command
 * line for a WebBEans SE app. Something like:
 * <code>
 * java -jar MyApp.jar org.jboss.webbeans.environment.se.StarMain arguments
 * </code>
 * @author Peter Royle
 */
public class StartMain
{

    private WebBeansBootstrap webBeansBootstrap;
    private String[] args;
    private boolean hasShutdownBeenCalled = false;
    Log log = Logging.getLog( StartMain.class );

    public StartMain( String[] commandLineArgs )
    {
        this.args = commandLineArgs;
    }

    private void go()
    {
        webBeansBootstrap = new WebBeansBootstrap( args );

        webBeansBootstrap.initialize();

        webBeansBootstrap.boot();

    }

    /**
     * The main method called from the command line. This little puppy
     * will get the ball rolling.
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {
        new StartMain( args ).go();
    }

    /**
     * The observer of the optional shutdown request which will in turn fire the
     * Shutdown event.
     * @param shutdownRequest
     */
    public void shutdown( @Observes ShutdownRequest shutdownRequest )
    {
        synchronized (this)
        {

            if (!hasShutdownBeenCalled)
            {
                hasShutdownBeenCalled = true;
                webBeansBootstrap.shutdown();
            } else
            {
                log.debug( "Skipping spurious call to shutdown");
                log.trace( Thread.currentThread().getStackTrace() );
            }
        }
    }

}
