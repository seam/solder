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
package org.jboss.solder.servlet.test.weld.event;

import javax.inject.Inject;
import javax.servlet.FilterChain;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletRequest;
import javax.servlet.ServletRequestEvent;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.servlet.ServletRequestContext;
import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.AbstractServletEventBridge;
import org.jboss.solder.servlet.event.ServletEventBridgeFilter;
import org.jboss.solder.servlet.event.ServletEventBridgeListener;
import org.jboss.solder.servlet.event.ServletEventBridgeServlet;
import org.jboss.solder.servlet.http.HttpServletRequestContext;
import org.jboss.solder.servlet.test.weld.event.ServletEventBridgeTestHelper.NoOpFilterChain;
import org.jboss.solder.servlet.test.weld.util.Deployments;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Nicklas Karlsson
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@RunWith(Arquillian.class)
public class ServletEventBridgeTest {
    @Deployment
    public static Archive<?> createDeployment() {
        return Deployments
                .createMockableBeanWebArchive()
                .addClasses(ServletEventBridgeTestHelper.class, ServletEventBridgeTest.class);
    }

    public static final FilterChain NOOP_FILTER_CHAIN = new NoOpFilterChain();

    @Inject
    ServletEventBridgeListener listener;

    @Inject
    ServletEventBridgeFilter filter;

    @Inject
    ServletEventBridgeServlet servlet;

    @Inject
    ServletEventBridgeTestHelper observer;

    // @Before
    public void reset() {
        observer.reset();
    }

    @Test
    public void should_observe_servlet_context() throws Exception {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        when(ctx.getServletContextName()).thenReturn("mock");
        ServletConfig cfg = mock(ServletConfig.class);
        when(cfg.getServletContext()).thenReturn(ctx);
        WebApplication webapp = new WebApplication(ctx);
        when(ctx.getAttribute(AbstractServletEventBridge.WEB_APPLICATION_ATTRIBUTE_NAME)).thenReturn(webapp);

        listener.contextInitialized(new ServletContextEvent(ctx));
        servlet.init(cfg);
        servlet.destroy();
        listener.contextDestroyed(new ServletContextEvent(ctx));
        observer.assertObservations("WebApplication", webapp, webapp, webapp);
        observer.assertObservations("ServletContext", ctx, ctx);
    }

    @Test
    public void should_observe_servlet_context_initialized() throws Exception {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        when(ctx.getServletContextName()).thenReturn("mock");
        ServletConfig cfg = mock(ServletConfig.class);
        when(cfg.getServletContext()).thenReturn(ctx);
        WebApplication webapp = new WebApplication(ctx);
        when(ctx.getAttribute(AbstractServletEventBridge.WEB_APPLICATION_ATTRIBUTE_NAME)).thenReturn(webapp);

        listener.contextInitialized(new ServletContextEvent(ctx));
        servlet.init(cfg);
        observer.assertObservations("@Initialized WebApplication", webapp);
        observer.assertObservations("@Initialized ServletContext", ctx);
        observer.assertObservations("@Started WebApplication", webapp);
    }

    @Test
    public void should_observe_servlet_context_destroyed() throws Exception {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        when(ctx.getServletContextName()).thenReturn("mock");
        ServletConfig cfg = mock(ServletConfig.class);
        when(cfg.getServletContext()).thenReturn(ctx);
        WebApplication webapp = new WebApplication(ctx);
        when(ctx.getAttribute(AbstractServletEventBridge.WEB_APPLICATION_ATTRIBUTE_NAME)).thenReturn(webapp);

        // the next call is needed to setup the WebApplication instance variable
        listener.contextInitialized(new ServletContextEvent(ctx));
        // the next call is needed to setup the ServletConfig instance variable
        servlet.init(cfg);
        observer.reset();

        servlet.destroy();
        observer.assertObservations("@Destroyed WebApplication", webapp);
        observer.assertObservations("@Destroyed ServletContext", ctx);
    }

    @Test
    public void should_observe_session() {
        reset();
        HttpSession session = mock(HttpSession.class);

        listener.sessionCreated(new HttpSessionEvent(session));
        listener.sessionWillPassivate(new HttpSessionEvent(session));
        listener.sessionDidActivate(new HttpSessionEvent(session));
        listener.sessionDestroyed(new HttpSessionEvent(session));
        observer.assertObservations("HttpSession", session, session, session, session);
    }

    @Test
    public void should_observe_session_created() {
        reset();
        HttpSession session = mock(HttpSession.class);

        listener.sessionCreated(new HttpSessionEvent(session));
        observer.assertObservations("@Initialized HttpSession", session);
    }

    @Test
    public void should_observe_session_destroyed() {
        reset();
        HttpSession session = mock(HttpSession.class);

        listener.sessionDestroyed(new HttpSessionEvent(session));
        observer.assertObservations("@Destroyed HttpSession", session);
    }

    @Test
    public void should_observe_session_will_passivate() {
        reset();
        HttpSession session = mock(HttpSession.class);

        listener.sessionWillPassivate(new HttpSessionEvent(session));
        observer.assertObservations("@WillPassivate HttpSession", session);
    }

    @Test
    public void should_observe_session_did_activate() {
        reset();
        HttpSession session = mock(HttpSession.class);

        listener.sessionDidActivate(new HttpSessionEvent(session));
        observer.assertObservations("@DidActivate HttpSession", session);
    }

    @Test
    public void should_observe_request() {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        ServletRequest req = mock(ServletRequest.class);
        when(req.getServletContext()).thenReturn(ctx);

        listener.requestInitialized(new ServletRequestEvent(ctx, req));
        listener.requestDestroyed(new ServletRequestEvent(ctx, req));
        observer.assertObservations("ServletRequest", req, req);
        observer.assertObservations("HttpServletRequest");
    }

    @Test
    public void should_observe_request_initialized() {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        ServletRequest req = mock(ServletRequest.class);
        when(req.getServletContext()).thenReturn(ctx);

        listener.requestInitialized(new ServletRequestEvent(ctx, req));
        observer.assertObservations("@Initialized ServletRequest", req);
        observer.assertObservations("@Initialized HttpServletRequest");
    }

    @Test
    public void should_observe_request_destroyed() {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        ServletRequest req = mock(ServletRequest.class);
        when(req.getServletContext()).thenReturn(ctx);

        listener.requestDestroyed(new ServletRequestEvent(ctx, req));
        observer.assertObservations("@Destroyed ServletRequest", req);
        observer.assertObservations("@Destroyed HttpServletRequest");
    }

    @Test
    public void should_observe_http_request() {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getServletContext()).thenReturn(ctx);

        listener.requestInitialized(new ServletRequestEvent(ctx, req));
        listener.requestDestroyed(new ServletRequestEvent(ctx, req));
        observer.assertObservations("ServletRequest", req, req);
        observer.assertObservations("HttpServletRequest", req, req);
    }

    @Test
    public void should_observe_http_request_initialized() {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getServletContext()).thenReturn(ctx);

        listener.requestInitialized(new ServletRequestEvent(ctx, req));
        observer.assertObservations("@Initialized ServletRequest", req);
        observer.assertObservations("@Initialized HttpServletRequest", req);
    }

    @Test
    public void should_observe_http_request_destroyed() {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getServletContext()).thenReturn(ctx);

        listener.requestDestroyed(new ServletRequestEvent(ctx, req));
        observer.assertObservations("@Destroyed ServletRequest", req);
        observer.assertObservations("@Destroyed HttpServletRequest", req);
    }

    @Test
    public void should_observe_servlet_request_context() throws Exception {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        ServletRequest req = mock(ServletRequest.class);
        ServletResponse res = mock(ServletResponse.class);
        ServletRequestContext rctx = new ServletRequestContext(req, res);
        when(req.getServletContext()).thenReturn(ctx);

        // the next call is needed to setup the ServletRequest instance variable
        listener.requestInitialized(new ServletRequestEvent(ctx, req));
        filter.doFilter(req, res, NoOpFilterChain.INSTANCE);
        observer.assertObservations("ServletResponse", res, res);
        observer.assertObservations("@Initialized ServletResponse", res);
        observer.assertObservations("@Destroyed ServletResponse", res);
        observer.assertObservations("ServletRequestContext", rctx, rctx);
        observer.assertObservations("@Initialized ServletRequestContext", rctx);
        observer.assertObservations("@Destroyed ServletRequestContext", rctx);
    }

    @Test
    public void should_observe_http_servlet_request_context() throws Exception {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse res = mock(HttpServletResponse.class);
        HttpServletRequestContext rctx = new HttpServletRequestContext(req, res);
        when(req.getServletContext()).thenReturn(ctx);

        // the next call is needed to setup the ServletRequest instance variable
        listener.requestInitialized(new ServletRequestEvent(ctx, req));
        filter.doFilter(req, res, NoOpFilterChain.INSTANCE);
        observer.assertObservations("ServletRequestContext", rctx, rctx);
        observer.assertObservations("@Initialized ServletRequestContext", rctx);
        observer.assertObservations("@Destroyed ServletRequestContext", rctx);
        observer.assertObservations("HttpServletRequestContext", rctx, rctx);
        observer.assertObservations("@Initialized HttpServletRequestContext", rctx);
        observer.assertObservations("@Destroyed HttpServletRequestContext", rctx);
        observer.assertObservations("ServletResponse", res, res);
        observer.assertObservations("@Initialized ServletResponse", res);
        observer.assertObservations("@Destroyed ServletResponse", res);
        observer.assertObservations("HttpServletResponse", res, res);
        observer.assertObservations("@Initialized HttpServletResponse", res);
        observer.assertObservations("@Destroyed HttpServletResponse", res);
    }

    @Test
    public void should_observe_http_request_initialized_for_path() {
        reset();
        ServletContext ctx = mock(ServletContext.class);
        HttpServletRequest req = mock(HttpServletRequest.class);
        when(req.getServletContext()).thenReturn(ctx);
        when(req.getServletPath()).thenReturn("/pathA");

        listener.requestInitialized(new ServletRequestEvent(ctx, req));
        observer.assertObservations("@Initialized @Path(\"pathA\") HttpServletRequest", req);
        observer.assertObservations("@Initialized @Path(\"pathB\") HttpServletRequest");
        observer.assertObservations("@Initialized HttpServletRequest", req);
    }
}
