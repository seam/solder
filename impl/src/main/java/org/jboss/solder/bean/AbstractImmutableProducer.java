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
package org.jboss.solder.bean;

import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.Producer;

import static java.util.Collections.unmodifiableSet;

/**
 * A base class for implementing {@link Producer}. The attributes are immutable,
 * and collections are defensively copied on instantiation.
 *
 * @author Pete Muir
 */
public abstract class AbstractImmutableProducer<T> implements Producer<T> {

    private final Set<InjectionPoint> injectionPoints;

    public AbstractImmutableProducer(Set<InjectionPoint> injectionPoints) {
        this.injectionPoints = new HashSet<InjectionPoint>(injectionPoints);
    }

    public Set<InjectionPoint> getInjectionPoints() {
        return unmodifiableSet(injectionPoints);
    }

}
