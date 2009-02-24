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

import java.net.URL;

public class FileDescriptor
{
    private String name;
    private URL url;

    public FileDescriptor( String name, URL url )
    {
        this.name = name;
        this.url = url;
    }

    public FileDescriptor( String name, ClassLoader classLoader )
    {
        this.name = name;

        if ( name == null )
        {
            throw new NullPointerException( "Name cannot be null, loading from " + classLoader );
        }

        this.url = classLoader.getResource( name );

        if ( this.url == null )
        {
            throw new NullPointerException( "Cannot find URL from classLoader for " + name + ", loading from " +
                                            classLoader );
        }
    }

    public String getName(  )
    {
        return name;
    }

    public URL getUrl(  )
    {
        return url;
    }

    @Override
    public String toString(  )
    {
        return url.getPath(  );
    }

    @Override
    public boolean equals( Object other )
    {
        if ( other instanceof FileDescriptor )
        {
            FileDescriptor that = (FileDescriptor) other;

            return this.getUrl(  ).equals( that.getUrl(  ) );
        } else
        {
            return false;
        }
    }

    @Override
    public int hashCode(  )
    {
        return getUrl(  ).hashCode(  );
    }
}
