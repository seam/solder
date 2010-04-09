package org.jboss.weld.extensions.resources.spi;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;

public interface ResourceLoader
{
   
   public URL getResource(String resource);
   
   public InputStream getResourceAsStream(String name);

   public Set<URL> getResources(String name);

}
