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
package org.jboss.webbeans.environment.se.util;

import java.util.Enumeration;
import java.util.NoSuchElementException;

public class EnumerationEnumeration<T>
    implements Enumeration<T>
{
    private Enumeration<T>[] enumerations;
    private int loc = 0;

    public EnumerationEnumeration( Enumeration<T>[] enumerations )
    {
        this.enumerations = enumerations;
    }

    public boolean hasMoreElements(  )
    {
        for ( int i = loc; i < enumerations.length; i++ )
        {
            if ( enumerations[i].hasMoreElements(  ) )
            {
                return true;
            }
        }

        return false;
    }

    public T nextElement(  )
    {
        while ( isCurrentEnumerationAvailable(  ) )
        {
            if ( currentHasMoreElements(  ) )
            {
                return currentNextElement(  );
            } else
            {
                nextEnumeration(  );
            }
        }

        throw new NoSuchElementException(  );
    }

    private void nextEnumeration(  )
    {
        loc++;
    }

    /*private boolean isNextEnumerationAvailable()
    {
       return loc < enumerations.length-1;
    }*/
    private boolean isCurrentEnumerationAvailable(  )
    {
        return loc < enumerations.length;
    }

    private T currentNextElement(  )
    {
        return enumerations[loc].nextElement(  );
    }

    private boolean currentHasMoreElements(  )
    {
        return enumerations[loc].hasMoreElements(  );
    }
}
