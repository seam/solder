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
package org.jboss.solder.core;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Veto the processing of the type if the required classes are not available.
 * Any beans defined by this class will not be installed.
 * </p>
 * <p/>
 * <p>
 * When placed on package, all beans in the package are installed if only if
 * all of required classes are available.
 * </p>
 * <p/>
 * <p>
 * If annotation is defined both on the package and the bean, union of
 * required classes defined by these annotations is considered.
 * </p>
 * <p/>
 * <p>
 * Solder will try both the Thread Context ClassLoader, as well as the
 * classloader of the declaring class.
 * </p>
 *
 * @author Stuart Douglas
 * @author Jozef Hartinger
 * @see Veto
 */
@Target({ElementType.TYPE, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Requires {
    String[] value();
}
