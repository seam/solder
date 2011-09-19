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

import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * An implementation of {@link InjectionTarget} that forwards all calls to
 * {@link #delegate()}.
 *
 * @param <T> The class of the instance
 * @author Pete Muir
 */
public abstract class ForwardingInjectionTarget<T> implements InjectionTarget<T> {

    /**
     * All calls to this {@link InjectionTarget} instance are forwarded to the
     * delegate unless overridden.
     *
     * @return the delegate {@link InjectionTarget}
     */
    protected abstract InjectionTarget<T> delegate();

    public void inject(T instance, CreationalContext<T> ctx) {
        delegate().inject(instance, ctx);
    }

    public void postConstruct(T instance) {
        delegate().postConstruct(instance);
    }

    public void preDestroy(T instance) {
        delegate().preDestroy(instance);
    }

    public void dispose(T instance) {
        delegate().dispose(instance);
    }

    public Set<InjectionPoint> getInjectionPoints() {
        return delegate().getInjectionPoints();
    }

    public T produce(CreationalContext<T> ctx) {
        return delegate().produce(ctx);
    }

}
