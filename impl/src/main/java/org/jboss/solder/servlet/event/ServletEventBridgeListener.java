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

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletRequestListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.jboss.solder.core.Requires;
import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.ImplicitServletObjectsHolder.InternalHttpSessionEvent;
import org.jboss.solder.servlet.event.ImplicitServletObjectsHolder.InternalServletContextEvent;
import org.jboss.solder.servlet.event.ImplicitServletObjectsHolder.InternalServletRequestEvent;
import org.jboss.solder.servlet.event.literal.DestroyedLiteral;
import org.jboss.solder.servlet.event.literal.DidActivateLiteral;
import org.jboss.solder.servlet.event.literal.HttpMethodLiteral;
import org.jboss.solder.servlet.event.literal.InitializedLiteral;
import org.jboss.solder.servlet.event.literal.PathLiteral;
import org.jboss.solder.servlet.event.literal.WillPassivateLiteral;

/**
 * Propagates Servlet lifecycle events to the CDI event bus.
 * <p/>
 * <p>
 * This listener is auto-registered in Servlet 3.0 environments. If CDI injection is available into listeners, the BeanManager
 * will be accessible to this instance as an injected resource. Otherwise, the BeanManager will be looked up using the
 * BeanManager provider service.
 * </p>
 * <p/>
 * <p>
 * The internal events are fired to ensure that the setup and tear down routines happen around the main events. The event
 * strategy is used to jump from a Servlet component which may not be managed by CDI to an observe we know to be a managed bean.
 * </p>
 *
 * @author Nicklas Karlsson
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Requires("javax.servlet.Servlet")
public class ServletEventBridgeListener extends AbstractServletEventBridge implements HttpSessionActivationListener,
        HttpSessionListener, ServletContextListener, ServletRequestListener {
    public void contextInitialized(final ServletContextEvent e) {
        fireEvent(new InternalServletContextEvent(e.getServletContext()), InitializedLiteral.INSTANCE);
        WebApplication webapp = new WebApplication(e.getServletContext());
        e.getServletContext().setAttribute(WEB_APPLICATION_ATTRIBUTE_NAME, webapp);
        fireEvent(webapp, InitializedLiteral.INSTANCE);
        fireEvent(e.getServletContext(), InitializedLiteral.INSTANCE);
    }

    public void contextDestroyed(final ServletContextEvent e) {
        fireEvent(new InternalServletContextEvent(e.getServletContext()), DestroyedLiteral.INSTANCE);
    }

    public void requestInitialized(final ServletRequestEvent e) {
        fireEvent(new InternalServletRequestEvent(e.getServletRequest()), InitializedLiteral.INSTANCE);
        if (e.getServletRequest() instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = HttpServletRequest.class.cast(e.getServletRequest());
            fireEvent(e.getServletRequest(), InitializedLiteral.INSTANCE, new PathLiteral(httpRequest.getServletPath()),
                    new HttpMethodLiteral(httpRequest.getMethod()));
        } else {
            fireEvent(e.getServletRequest(), InitializedLiteral.INSTANCE);
        }
    }

    public void requestDestroyed(final ServletRequestEvent e) {
        if (e.getServletRequest() instanceof HttpServletRequest) {
            HttpServletRequest httpRequest = HttpServletRequest.class.cast(e.getServletRequest());
            fireEvent(e.getServletRequest(), DestroyedLiteral.INSTANCE, new PathLiteral(httpRequest.getServletPath()),
                    new HttpMethodLiteral(httpRequest.getMethod()));
        } else {
            fireEvent(e.getServletRequest(), DestroyedLiteral.INSTANCE);
        }
        fireEvent(new InternalServletRequestEvent(e.getServletRequest()), DestroyedLiteral.INSTANCE);
    }

    public void sessionCreated(final HttpSessionEvent e) {
        fireEvent(new InternalHttpSessionEvent(e.getSession()), InitializedLiteral.INSTANCE);
        fireEvent(e.getSession(), InitializedLiteral.INSTANCE);
    }

    public void sessionDestroyed(final HttpSessionEvent e) {
        fireEvent(e.getSession(), DestroyedLiteral.INSTANCE);
        fireEvent(new InternalHttpSessionEvent(e.getSession()), DestroyedLiteral.INSTANCE);
    }

    public void sessionDidActivate(final HttpSessionEvent e) {
        fireEvent(e.getSession(), DidActivateLiteral.INSTANCE);
    }

    public void sessionWillPassivate(final HttpSessionEvent e) {
        fireEvent(e.getSession(), WillPassivateLiteral.INSTANCE);
    }
}
