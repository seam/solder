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
package org.jboss.webbeans.environment.se.beans;

import org.jboss.webbeans.environment.se.bindings.Parameters;

import java.util.ArrayList;
import java.util.Arrays;

import javax.context.ApplicationScoped;
import javax.inject.Produces;

/**
 * The simple bean that will hold the command line arguments
 * and make them available by injection (using the @Parameters binding).
 * It's initialised by the StartMain class before your main app is
 * initialised.
 * @author Peter Royle
 */
@ApplicationScoped
public class ParametersFactory
{
    private String[] args;

    /**
     * Producer method for the injectible command line args.
     * @return The command line arguments.
     */
    @Produces
    @Parameters
    public ArrayList<String> getArgs(  )
    {
        // TODO (PR): is there an unmodifiable, serializable List?
        return new ArrayList( Arrays.asList( this.args ) );
    }

    /**
     * StartMain passes in the command line args here.
     * @param args The command line arguments.
     */
    public void setArgs( String[] args )
    {
        this.args = args;
    }
}
