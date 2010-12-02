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
package org.jboss.seam.solder.servicehandler;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Meta annotation that is used to specify an invocation handler for
 * automatically implemented bean.
 * </p>
 * 
 * <p>
 * If the annotation that is annotated with this meta-annotation is applied to
 * an interface or abstract class then the container will automatically provide
 * a concrete implementation of the class/interface, and delegate all calls to
 * abstract methods to the handler class specified by this annotations.
 * </p>
 * 
 * <p>
 * The handler class must have a method with the following signature:
 * </p>
 * 
 * <pre>
 *    &#64;AroundInvoke public Object aroundInvoke(final InvocationContext invocation) throws Exception
 * </pre>
 * 
 * <p>
 * Initializer methods and <code>&#64;PostConstruct</code> methods declared on the invocation
 * handler will be called, however <code>&#64;PreDestory</code> methods will not be called.
 * </p>
 * 
 * <p>
 * The annotation should have:
 * </p>
 * 
 * <pre>
 * &#64;Retention(RUNTIME)
 * &#64;Target({ TYPE })
 * </pre>
 * 
 * @author Stuart Douglas <stuart.w.douglas@gmail.com>
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
@Documented
public @interface ServiceHandler
{
   Class<?> value();
}
