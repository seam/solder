/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
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
package org.jboss.seam.solder.resourceLoader;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.io.InputStream;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.net.URL;
import java.util.Properties;

import javax.enterprise.util.Nonbinding;
import javax.inject.Qualifier;

/**
 * <p>
 * An injection point qualifier that may be used to specify a resource to
 * inject. The injection point can specify either a {@link URL}, an
 * {@link InputStream} or a {@link Properties} bundle (if the resource points to
 * a properties bundle). For example:
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
 * If a input stream is loaded, it will be automatically closed when the
 * InputStream goes out of scope. If a URL is used to create an input stream,
 * the application is responsible for closing it. For this reason it is
 * recommended that managed input streams are used where possible.
 * </p>
 * 
 * <p>
 * If you don't know the name of the resource to load at development time, then
 * you may wish to use {@link ResourceProvider} which can dynamically load
 * resources.
 * </p>
 * 
 * @author Pete Muir
 * 
 * @see ResourceProvider
 */
@Retention(RUNTIME)
@Target({ METHOD, TYPE, FIELD, PARAMETER })
@Documented
@Qualifier
public @interface Resource
{

   @Nonbinding
   String value();

}
