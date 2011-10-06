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
package org.jboss.solder.servlet.event;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.solder.core.Requires;
import org.jboss.solder.servlet.ServletRequestContext;
import org.jboss.solder.servlet.event.ImplicitServletObjectsHolder.InternalServletResponseEvent;
import org.jboss.solder.servlet.event.literal.DestroyedLiteral;
import org.jboss.solder.servlet.event.literal.InitializedLiteral;
import org.jboss.solder.servlet.event.literal.PathLiteral;
import org.jboss.solder.servlet.http.HttpServletRequestContext;

/**
 * Propagates the {@link ServletResponse} lifecycle events to the CDI event bus, complementing the ServletEventBridgeListener,
 * which handles the other lifecycle events.
 * <p/>
 * <p>
 * This filter is auto-registered in Servlet 3.0 environments. If CDI injection is available into filters, the BeanManager will
 * be accessible to this instance as an injected resource. Otherwise, the BeanManager will be looked up using the BeanManager
 * provider service.
 * </p>
 * <p/>
 * <p>
 * The internal events are fired to ensure that the setup and tear down routines happen around the main events. The event
 * strategy is used to jump from a Servlet component which may not be managed by CDI to an observe we know to be a managed bean.
 * </p>
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Requires("javax.servlet.Servlet")
public class ServletEventBridgeFilter extends AbstractServletEventBridge implements Filter {
    public void init(FilterConfig config) throws ServletException {
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
            ServletException {
        fireEvent(new InternalServletResponseEvent(response), InitializedLiteral.INSTANCE);
        Path path = null;
        if (request instanceof HttpServletRequest) {
            path = new PathLiteral(HttpServletRequest.class.cast(request).getServletPath());
            fireEvent(response, InitializedLiteral.INSTANCE, path);
            fireEvent(new HttpServletRequestContext(request, response), InitializedLiteral.INSTANCE, path);
        } else {
            fireEvent(response, InitializedLiteral.INSTANCE);
            fireEvent(new ServletRequestContext(request, response), InitializedLiteral.INSTANCE);
        }

        try {
            if (!response.isCommitted()) {
                chain.doFilter(request, response);
            }
        } finally {
            if (request instanceof HttpServletRequest) {
                fireEvent(response, DestroyedLiteral.INSTANCE, path);
                fireEvent(new HttpServletRequestContext(request, response), DestroyedLiteral.INSTANCE, path);
            } else {
                fireEvent(response, DestroyedLiteral.INSTANCE);
                fireEvent(new ServletRequestContext(request, response), DestroyedLiteral.INSTANCE);
            }
            fireEvent(new InternalServletResponseEvent(response), DestroyedLiteral.INSTANCE);
        }
    }

    public void destroy() {
    }
}
