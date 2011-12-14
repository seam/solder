package org.jboss.solder.servlet.resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.jboss.solder.logging.Logger;
import org.jboss.solder.util.Sortable;
import org.jboss.solder.util.service.ServiceLoader;

/**
 * <p>
 * A utility for classes that need to access web resources.
 * </p>
 * <p>
 * This class provides a way to obtain the location of web resources without using the {@link ServletContext}. This is
 * especially interesting for extensions that are executed very early in the CDI startup process because the
 * {@link ServletContext} may not be available in this stage.
 * </p>
 * <p>
 * The class makes use of the {@link WebResourceLocationProvider} SPI to actually find the resources. This allows to write
 * custom implementations optimized for specific environments.
 * </p>
 *
 * @author Christian Kaltepoth
 * @see WebResourceLocationProvider
 */
public class WebResourceLocator {

    private final Logger log = Logger.getLogger(WebResourceLocator.class);

    /**
     * Returns the resource located at the named path as an <code>InputStream</code> object. The path must begin with a
     * <tt>/</tt> and is interpreted as relative to the current context root.
     *
     * @param path The path of the resource (e.g. "/WEB-INF/web.xml")
     * @return the <code>InputStream</code> or <code>null</code> if the resource could not be located
     */
    public InputStream getWebResource(String path) {
        // execute the SPI implementation
        URL resourceLocation = getWebResourceUrl(path);

        // accept the first result
        if (resourceLocation != null) {

            // try to open an InputStream
            try {
                return resourceLocation.openStream();
            }
            // log failure and continue with next provider
            catch (IOException e) {
                if (log.isDebugEnabled()) {
                    log.debug("Error opening: " + resourceLocation.toString(), e);
                }
            }
        }
        return null;
    }

    /**
     * Returns the <code>URL</code> for the resource located at the named path. The path must begin with a
     * <tt>/</tt> and is interpreted as relative to the current context root.
     *
     * @param path The path of the resource (e.g. "/WEB-INF/web.xml")
     * @return the <code>URL</code> or <code>null</code> if the resource could not be located
     */
    public URL getWebResourceUrl(final String path) {
        // build sorted list of provider implementations
        List<WebResourceLocationProvider> providers = new ArrayList<WebResourceLocationProvider>();
        Iterator<WebResourceLocationProvider> iterator = ServiceLoader.load(WebResourceLocationProvider.class).iterator();
        while (iterator.hasNext()) {
            providers.add(iterator.next());
        }
        Collections.sort(providers, new Sortable.Comparator());

        // prefer the context classloader
        ClassLoader classLoader = WebResourceLocator.class.getClassLoader();

        // process each provider one by one
        for (WebResourceLocationProvider provider : providers) {

            // execute the SPI implementation
            final URL resourceLocation = provider.getWebResource(path, classLoader);

            if (resourceLocation != null) {
                return resourceLocation;
            }
        }
        return null;
    }
}
