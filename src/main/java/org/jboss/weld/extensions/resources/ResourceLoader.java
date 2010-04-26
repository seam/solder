package org.jboss.weld.extensions.resources;

import java.io.InputStream;
import java.net.URL;
import java.util.Set;

import org.jboss.weld.extensions.util.Sortable;

public interface ResourceLoader extends Sortable
{
   
   public URL getResource(String resource);
   
   public InputStream getResourceAsStream(String name);

   public Set<URL> getResources(String name);

}
