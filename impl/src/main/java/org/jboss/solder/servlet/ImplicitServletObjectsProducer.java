/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.solder.servlet;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.solder.core.Requires;
import org.jboss.solder.servlet.event.ImplicitServletObjectsHolder;

/**
 * Produces an application-scoped {@link ServletContext} bean. A references is obtained from the
 * {@link ImplicitServletObjectsHolder}.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 * @author Shane Bryzak
 */
@Requires("javax.servlet.Servlet")
public class ImplicitServletObjectsProducer {
    @Inject
    private ImplicitServletObjectsHolder holder;

    @Produces
    @ApplicationScoped
    protected ServletContext getServletContext() {
        return holder.getServletContext();
    }

    @Produces
    @RequestScoped
    protected ServletRequestContext getServletRequestContext() {
        return holder.getServletRequestContext();
    }

    @Produces
    @RequestScoped
    protected ServletRequest getServletRequest() {
        return holder.getServletRequest();
    }

    @Produces
    @RequestScoped
    protected ServletResponse getServletResponse() throws IllegalStateException {
        if (holder.getServletResponse() == null) {
            throw new IllegalStateException("Attempted to inject a ServletResponse before it has been initialized.");
        }
        
        return holder.getServletResponse();
    }

    @Produces
    @ServerInfo
    protected String getServerInfo() {
        return getServletContext().getServerInfo();
    }
}
