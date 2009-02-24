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

import org.jboss.webbeans.environment.se.util.EnumerationEnumeration;
import static org.jboss.webbeans.util.Strings.split;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

public class WebBeansDeploymentProperties
{
    private ClassLoader classLoader;
    private Enumeration<URL> urlEnum;

    public WebBeansDeploymentProperties( ClassLoader classLoader )
    {
        this.classLoader = classLoader;
    }

    /**
     * The resource bundle used to control Seam deployment
     */
    public static final String RESOURCE_BUNDLE = "META-INF/seam-deployment.properties";

    // All resource bundles to use, including legacy names
    private static final String[] RESOURCE_BUNDLES = { RESOURCE_BUNDLE, "META-INF/seam-scanner.properties" };

    /**
     * Get a list of possible values for a given key.
     *
     * First, System properties are tried, followed by the specified resource
     * bundle (first in classpath only).
     *
     * Colon (:) deliminated lists are split out.
     *
     */
    public List<String> getPropertyValues( String key )
    {
        List<String> values = new ArrayList<String>(  );
        addPropertiesFromSystem( key, values );
        addPropertiesFromResourceBundle( key, values );

        return values;
    }

    private void addPropertiesFromSystem( String key, List<String> values )
    {
        addProperty( key,
                     System.getProperty( key ),
                     values );
    }

    private void addPropertiesFromResourceBundle( String key, List<String> values )
    {
        try
        {
            while ( getResources(  ).hasMoreElements(  ) )
            {
                URL url = getResources(  ).nextElement(  );
                Properties properties = new Properties(  );
                InputStream propertyStream = url.openStream(  );

                try
                {
                    properties.load( propertyStream );
                    addProperty( key,
                                 properties.getProperty( key ),
                                 values );
                } finally
                {
                    if ( propertyStream != null )
                    {
                        propertyStream.close(  );
                    }
                }
            }
        } catch ( IOException e )
        {
            // No - op, file is optional
        }
    }

    /*
     * Add the property to the set of properties only if it hasn't already been added
     */
    private void addProperty( String key, String value, List<String> values )
    {
        if ( value != null )
        {
            String[] properties = split( value, ":" );

            for ( String property : properties )
            {
                values.add( property );
            }
        }
    }

    private Enumeration<URL> getResources(  )
                                   throws IOException
    {
        if ( urlEnum == null )
        {
            Enumeration<URL>[] enumerations = new Enumeration[RESOURCE_BUNDLES.length];

            for ( int i = 0; i < RESOURCE_BUNDLES.length; i++ )
            {
                enumerations[i] = classLoader.getResources( RESOURCE_BUNDLES[i] );
            }

            urlEnum = new EnumerationEnumeration<URL>( enumerations );
        }

        return urlEnum;
    }
}
