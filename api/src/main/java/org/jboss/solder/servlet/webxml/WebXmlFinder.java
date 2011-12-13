package org.jboss.solder.servlet.webxml;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;

import org.jboss.solder.util.Sortable;
import org.jboss.solder.util.service.ServiceLoader;

/**
 * <p>
 * A utility for classes that need to access <code>web.xml</code>.
 * </p>
 * <p>
 * This class provides a way to obtain the location of the <code>web.xml</code> without using the {@link ServletContext}. This
 * is especially interesting for extensions that are executed very early in the CDI startup process because the
 * {@link ServletContext} may not be available in this stage.
 * </p>
 * <p>
 * The class makes use of the {@link WebXmlLocator} SPI to actually find the <code>web.xml</code>. This allows to write custom
 * implementations optimized for specific environments.
 * </p>
 * 
 * @author Christian Kaltepoth
 * @see WebXmlLocator
 * 
 */
public class WebXmlFinder {

    /**
     * set as soon as the lookup has been performed
     */
    private volatile boolean lookupDone = false;

    /**
     * location of web.xml which is looked up lazily
     */
    private volatile URL webXmlLocation = null;

    /**
     * Try to obtain the location of <code>web.xml</code>. If the lookup hasn't been performed this method will consult all
     * implementations of {@link WebXmlLocator} until it gets a result.
     * 
     * @return The location of <code>web.xml</code> or <code>null</code> if could not be found
     */
    public URL getWebXmlLocation() {

        if (!lookupDone) {
            lookupWebXmlLocation();
        }

        return webXmlLocation;

    }

    /**
     * Checks whether {@link WebXmlFinder} was able to find the location of <code>web.xml</code>.
     * 
     * @return <code>true</code> if the location could be found
     */
    public boolean isWebXmlLocationAvailable() {

        if (!lookupDone) {
            lookupWebXmlLocation();
        }

        return webXmlLocation != null;

    }

    /**
     * internal method to lazily perform the lookup of the <code>web.xml</code> location
     */
    private synchronized void lookupWebXmlLocation() {

        if (!lookupDone) {

            // build sorted list of locator implementations
            List<WebXmlLocator> locators = new ArrayList<WebXmlLocator>();
            for (Iterator<WebXmlLocator> iter = ServiceLoader.load(WebXmlLocator.class).iterator(); iter.hasNext();) {
                locators.add(iter.next());
            }
            Collections.sort(locators, new Sortable.Comparator());

            // prefer the context classloader
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }

            // process each locator one by one
            for (WebXmlLocator locator : locators) {

                // execute the SPI implementation
                webXmlLocation = locator.getWebXmlLocation(classLoader);

                // accept the first result
                if (webXmlLocation != null) {
                    break;
                }

            }

            lookupDone = true;

        }

    }

}
