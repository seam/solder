/**
 * <p>
 * An extensible, injectable resource loader that can provide provide URLs, managed input streams and sets of 
 * properties.
 * </p>
 * 
 * <p>
 * If the resource name is known at development time, the resource can be injected:
 * </p>
 * 
 * <pre>
 * &#064;Inject
 * &#064;Resource(&quot;WEB-INF/beans.xml&quot;)
 * URL beansXml;
 * 
 * &#064;Inject
 * &#064;Resource(&quot;WEB-INF/web.xml&quot;)
 * InputStream webXml;
 * 
 * &#064;Inject
 * &#064;Resource(&quot;META-INF/aws.properties&quot;)
 * Properties awsProperties;
 * </pre>
 * 
 * <p>
 * If the resource name is not known, the {@link org.jboss.seam.solder.resourceLoader.ResourceProvider}
 * can be injected, and the resource looked up dynamically:
 * </p>
 * 
 * <pre>
 * &#64;Inject
 * void readXml(ResourceProvider provider, String fileName) {
 *    InputStream webXml = provider.loadResourceStream(fileName);
 * }
 * </pre>
 * 
 * <p>
 * Any input stream injected, or created directly by the {@link org.jboss.seam.solder.resourceLoader.ResourceProvider}
 * is managed, and will be automatically closed when the bean declaring the injection point of the resource
 * or provider is destroyed. 
 * </p>
 * 
 * @author Pete Muir
 * 
 * @see org.jboss.seam.solder.resourceLoader.Resource
 * @see org.jboss.seam.solder.resourceLoader.ResourceProvider
 * @see org.jboss.seam.solder.resourceLoader.ResourceLoader
 * @see org.jboss.seam.solder.resourceLoader.ResourceLoaderManager
 */
package org.jboss.seam.solder.resourceLoader;

