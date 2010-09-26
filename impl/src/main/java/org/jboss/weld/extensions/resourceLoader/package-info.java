/**
 * <p>
 * Weld Extensions provides an extensible, injectable resource loader. The resource loader 
 * can provide URLs or managed input streams.
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
 * </pre>
 * 
 * <p>
 * If the resource name is not known, the {@link org.jboss.weld.extensions.resourceLoader.ResourceProvider}
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
 * Any input stream injected, or created directly by the {@link org.jboss.weld.extensions.resourceLoader.ResourceProvider}
 * is managed, and will be automatically closed when the bean declaring the injection point of the resource
 * or provider is destroyed. 
 * </p>
 * 
 * @author Pete Muir
 * 
 * @see org.jboss.weld.extensions.resourceLoader.Resource
 * @see org.jboss.weld.extensions.resourceLoader.ResourceProvider
 * @see org.jboss.weld.extensions.resourceLoader.ResourceLoader
 */
package org.jboss.weld.extensions.resourceLoader;

