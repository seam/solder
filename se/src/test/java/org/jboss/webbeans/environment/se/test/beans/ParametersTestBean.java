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

import java.util.List;

import javax.context.ApplicationScoped;
import javax.inject.Initializer;

import org.jboss.webbeans.environment.se.bindings.Parameters;
import org.testng.Assert;

/**
 *
 * @author Peter Royle
 */
@ApplicationScoped
public class ParametersTestBean {

    List<String> parameters;

    @Initializer
    public void init(@Parameters List<String> params) {
        this.parameters = params;
        // even if no args are given, it should will always at least be an empty array
        Assert.assertNotNull( params );
    }

    public List<String> getParameters()
    {
        return parameters;
    }

}
