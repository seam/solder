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

import org.jboss.webbeans.context.beanmap.SimpleBeanMap;
import org.jboss.webbeans.environment.se.boot.WebBeansBootstrap;
import org.jboss.webbeans.lifecycle.ApplicationLifecycle;

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

    String[] args;

    public StartMain( String[] commandLineArgs )
    {
        this.args = commandLineArgs;
    }

    public void go()
    {
        WebBeansBootstrap webBeansBootstrap = new WebBeansBootstrap( args );

        webBeansBootstrap.initialize();

        ApplicationLifecycle lifecycle = ApplicationLifecycle.instance();
        lifecycle.initialize();
        final SimpleBeanMap appBeanMap = new SimpleBeanMap();
        lifecycle.beginApplication( "TODO: application id?", appBeanMap );

        webBeansBootstrap.boot();

        lifecycle.endApplication( "TODO: application id?", appBeanMap );

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
     * When an exception happens in your main app, you don't want
     * the manager to come down in a screaming heap. Well, not all the time.
     * Sometimes you just want to exit gently. This method will detect such
     * times and behave appropriately.
     * @param e
     */
//    private void handleException( Throwable e )
//    {
//        // check for a 'special' root cause
//        boolean dealtWith = false;
//        Throwable cause = e.getCause();
//
//        while (cause != null)
//        {
//            if (cause instanceof CleanShutdownException)
//            {
//                // This is a request to shut down silently, so swollow it.
//                // Hoewver, if there's a message, then print it to stdout
//                if (cause.getMessage() != null)
//                {
//                    System.out.println( cause.getMessage() );
//                }
//
//                dealtWith = true;
//            }
//
//            cause = cause.getCause();
//        }
//
//        if (!dealtWith)
//        {
//            e.printStackTrace();
//        }
//    }
}
