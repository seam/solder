/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.solder.unwraps;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Produces;

/**
 * <p>
 * Identifies a stateless producer method where each method invocation on the
 * produced object will cause the annotated method to be invoked to produce the
 * object.
 * </p>
 * <p/>
 * <p>
 * A method is annotated with <code>&#064;Unwraps</code> is registered with CDI
 * as bean; whenever a method is invoked on the proxy the @Unwraps method is
 * called, and the method invocation is forwarded to the result. This allows you
 * to manual control the lifecycle of the object while still allowing it to be
 * injected.
 * </p>
 * <p/>
 * <p>
 * As the method is called every time a method invocation occurs, it is
 * important that you do not perform expensive operations in this method.
 * Normally you will want to simply expose an existing object via unwrap method:
 * </p>
 * <p/>
 * <pre>
 * &#064SessionScoped
 * class FooManager {
 *
 *    private Foo foo;
 *
 *    &#064;PostConstruct
 *    void init() {
 *       // set up Foo
 *    }
 *
 *    Foo getFoo() {
 *       return foo;
 *    }
 *
 *    // Client immediately reflect any changes to Bar as a result
 *    // of changes to Foo
 *    &#064;Unwraps
 *    Bar getBar() {
 *       return foo.getBar();
 *    }
 *
 * }
 * </pre>
 * <p/>
 * <p>
 * The return type of the annotated method must be proxyable (see Section 5.4.1
 * of the CDI specification, "Unproxyable bean types"). The method must not have
 * a scope annotation.
 * </p>
 *
 * @author Stuart Douglas
 * @author Pete Muir
 * @see Produces
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Unwraps {

}
