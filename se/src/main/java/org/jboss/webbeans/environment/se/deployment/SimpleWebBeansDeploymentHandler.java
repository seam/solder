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
package org.jboss.webbeans.environment.se.deployment;

import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.Set;

/**
 * The {@link SimpleWebBeansDeploymentHandler} processes all classes found in a
 * Web Beans archive/folder.
 *
 * @author Pete Muir
 * @author Peter Royle
 *
 */
public class SimpleWebBeansDeploymentHandler
    extends AbstractClassDeploymentHandler
{
    public static ClassDeploymentMetadata NAME_ANNOTATED_CLASS_METADATA =
        new ClassDeploymentMetadata(  )
        {
            public Set<Class<?extends Annotation>> getClassAnnotatedWith(  )
            {
                return Collections.EMPTY_SET;
            }

            public String getFileNameSuffix(  )
            {
                return null;
            }
        };

    /**
     * Name under which this {@link DeploymentHandler} is registered
     */
    public static final String NAME = "org.jboss.webbeans.environment.se.deployment.SimpleWebBeansDeploymentHandler";

    public String getName(  )
    {
        return NAME;
    }

    public ClassDeploymentMetadata getMetadata(  )
    {
        return NAME_ANNOTATED_CLASS_METADATA;
    }
}
