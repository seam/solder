/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
 * If the resource name is not known, the {@link org.jboss.solder.resourceLoader.ResourceProvider}
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
 * Any input stream injected, or created directly by the {@link org.jboss.solder.resourceLoader.ResourceProvider}
 * is managed, and will be automatically closed when the bean declaring the injection point of the resource
 * or provider is destroyed.
 * </p>
 *
 * @author Pete Muir
 *
 * @see org.jboss.solder.resourceLoader.Resource
 * @see org.jboss.solder.resourceLoader.ResourceProvider
 * @see org.jboss.solder.resourceLoader.ResourceLoader
 * @see org.jboss.solder.resourceLoader.ResourceLoaderManager
 */
package org.jboss.solder.resourceLoader;

