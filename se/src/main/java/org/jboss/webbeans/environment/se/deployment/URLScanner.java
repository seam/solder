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

import org.jboss.webbeans.log.LogProvider;
import org.jboss.webbeans.log.Logging;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLDecoder;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Implementation of {@link Scanner} which can scan a {@link URLClassLoader}
 *
 * @author Thomas Heute
 * @author Gavin King
 * @author Norman Richards
 * @author Pete Muir
 *
 */
public class URLScanner
    extends AbstractScanner
{
    private static final LogProvider log = Logging.getLogProvider( URLScanner.class );

    public URLScanner( Set<DeploymentHandler> deploymentHandlers, ClassLoader classLoader )
    {
        super( deploymentHandlers, classLoader );
    }

    @Override
    public void scanDirectories( File[] directories )
    {
        for ( File directory : directories )
        {
            handleDirectory( directory, null );
        }
    }

    public void scanResources( String[] resources )
    {
        Set<String> paths = new HashSet<String>(  );

        for ( String resourceName : resources )
        {
            try
            {
                Enumeration<URL> urlEnum = getClassLoader(  ).getResources( resourceName );

                while ( urlEnum.hasMoreElements(  ) )
                {
                    String urlPath = urlEnum.nextElement(  ).getFile(  );
                    urlPath = URLDecoder.decode( urlPath, "UTF-8" );

                    if ( urlPath.startsWith( "file:" ) )
                    {
                        urlPath = urlPath.substring( 5 );
                    }

                    if ( urlPath.indexOf( '!' ) > 0 )
                    {
                        urlPath =
                            urlPath.substring( 0,
                                               urlPath.indexOf( '!' ) );
                    } else
                    {
                        File dirOrArchive = new File( urlPath );

                        if ( ( resourceName != null ) && ( resourceName.lastIndexOf( '/' ) > 0 ) )
                        {
                            //for META-INF/components.xml
                            dirOrArchive = dirOrArchive.getParentFile(  );
                        }

                        urlPath = dirOrArchive.getParent(  );
                    }

                    paths.add( urlPath );
                }
            } catch ( IOException ioe )
            {
                log.warn( "could not read: " + resourceName, ioe );
            }
        }

        handle( paths );
    }

    protected void handle( Set<String> paths )
    {
        for ( String urlPath : paths )
        {
            try
            {
                log.trace( "scanning: " + urlPath );

                File file = new File( urlPath );

                if ( file.isDirectory(  ) )
                {
                    handleDirectory( file, null );
                } else
                {
                    handleArchiveByFile( file );
                }
            } catch ( IOException ioe )
            {
                log.warn( "could not read entries", ioe );
            }
        }
    }

    private void handleArchiveByFile( File file )
                              throws IOException
    {
        try
        {
            log.trace( "archive: " + file );

            ZipFile zip = new ZipFile( file );
            Enumeration<?extends ZipEntry> entries = zip.entries(  );

            while ( entries.hasMoreElements(  ) )
            {
                ZipEntry entry = entries.nextElement(  );
                String name = entry.getName(  );
                handle( name );
            }
        } catch ( ZipException e )
        {
            throw new RuntimeException( "Error handling file " + file, e );
        }
    }

    private void handleDirectory( File file, String path )
    {
        handleDirectory( file,
                         path,
                         new File[0] );
    }

    private void handleDirectory( File file, String path, File[] excludedDirectories )
    {
        for ( File excludedDirectory : excludedDirectories )
        {
            if ( file.equals( excludedDirectory ) )
            {
                log.trace( "skipping excluded directory: " + file );

                return;
            }
        }

        log.trace( "handling directory: " + file );

        for ( File child : file.listFiles(  ) )
        {
            String newPath = ( path == null ) ? child.getName(  ) : ( path + '/' + child.getName(  ) );

            if ( child.isDirectory(  ) )
            {
                handleDirectory( child, newPath, excludedDirectories );
            } else
            {
                handle( newPath );
            }
        }
    }


}
