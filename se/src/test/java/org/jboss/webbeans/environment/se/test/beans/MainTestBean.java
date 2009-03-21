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
package org.jboss.webbeans.environment.se.test.beans;

import javax.event.Observes;
import javax.inject.Current;
import javax.inject.Initializer;
import javax.inject.manager.Deployed;
import javax.inject.manager.Manager;

/**
 *
 * @author Peter Royle
 */
public class MainTestBean
{

    boolean initialised = false;
    ParametersTestBean parametersTestBean;

    @Initializer
    public void init(@Current ParametersTestBean paramsTestBean)
    {
        this.initialised = true;
        this.parametersTestBean = paramsTestBean;
        // this call is important. It invokes initialiser on the proxy
        paramsTestBean.getParameters();
    }

    public void mainMethod(@Observes @Deployed Manager manager) {
        System.out.println( "Starting main test app" );
    }

    public ParametersTestBean getParametersTestBean()
    {
        return parametersTestBean;
    }

    public boolean isInitialised()
    {
        return initialised;
    }
    
}
