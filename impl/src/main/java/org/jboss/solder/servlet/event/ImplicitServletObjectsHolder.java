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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.BeanManager;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.solder.core.Requires;
import org.jboss.solder.logging.Category;
import org.jboss.solder.servlet.ServletRequestContext;
import org.jboss.solder.servlet.beanManager.ServletContextAttributeProvider;
import org.jboss.solder.servlet.http.HttpServletRequestContext;
import org.jboss.solder.servlet.support.ServletLogger;

/**
 * A manager for tracking the contextual Servlet objects, specifically the {@link ServletContext}, {@link HttpServletRequest}
 * and {@link HttpServletResponse}.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Requires("javax.servlet.Servlet")
@ApplicationScoped
public class ImplicitServletObjectsHolder {
    @Inject
    @Category(ServletLogger.CATEGORY)
    private ServletLogger log;

    private ServletContext servletCtx;

    private final ThreadLocal<ServletRequestContext> requestCtx = new ThreadLocal<ServletRequestContext>() {
        @Override
        protected ServletRequestContext initialValue() {
            return null;
        }
    };

    protected void contextInitialized(@Observes @Initialized final InternalServletContextEvent e, BeanManager beanManager) {
        ServletContext ctx = e.getServletContext();
        log.servletContextInitialized(ctx);
        ctx.setAttribute(BeanManager.class.getName(), beanManager);
        ServletContextAttributeProvider.setServletContext(ctx);
        servletCtx = ctx;
    }

    protected void contextDestroyed(@Observes @Destroyed final InternalServletContextEvent e) {
        log.servletContextDestroyed(e.getServletContext());
        servletCtx = null;
    }

    protected void requestInitialized(@Observes @Initialized final InternalServletRequestEvent e) {
        ServletRequest req = e.getServletRequest();
        log.servletRequestInitialized(req);
        ServletContextAttributeProvider.setServletContext(servletCtx);
        if (req instanceof HttpServletRequest) {
            requestCtx.set(new HttpServletRequestContext(req));
        } else {
            requestCtx.set(new ServletRequestContext(req));
        }
    }

    protected void requestDestroyed(@Observes @Destroyed final InternalServletRequestEvent e) {
        log.servletRequestDestroyed(e.getServletRequest());
        ServletContextAttributeProvider.setServletContext(null);
        requestCtx.set(null);
    }

    protected void responseInitialized(@Observes @Initialized final InternalServletResponseEvent e) {
        ServletResponse res = e.getServletResponse();
        log.servletResponseInitialized(res);
        if (res instanceof HttpServletResponse) {
            requestCtx.set(new HttpServletRequestContext(requestCtx.get().getRequest(), res));
        } else {
            requestCtx.set(new ServletRequestContext(requestCtx.get().getRequest(), res));
        }
    }

    protected void responseDestroyed(@Observes @Destroyed final InternalServletResponseEvent e) {
        log.servletResponseDestroyed(e.getServletResponse());
        if (requestCtx.get() instanceof HttpServletRequestContext) {
            requestCtx.set(new HttpServletRequestContext(requestCtx.get().getRequest()));
        } else {
            requestCtx.set(new ServletRequestContext(requestCtx.get().getRequest()));
        }
    }

    public ServletContext getServletContext() {
        return servletCtx;
    }

    public ServletRequestContext getServletRequestContext() {
        return requestCtx.get();
    }

    public HttpServletRequestContext getHttpServletRequestContext() {
        if (requestCtx.get() instanceof HttpServletRequestContext) {
            return HttpServletRequestContext.class.cast(requestCtx.get());
        } else {
            return null;
        }
    }

    public ServletRequest getServletRequest() {
        if (requestCtx.get() != null) {
            return requestCtx.get().getRequest();
        } else {
            return null;
        }
    }

    public HttpServletRequest getHttpServletRequest() {
        if (requestCtx.get() instanceof HttpServletRequestContext) {
            return HttpServletRequestContext.class.cast(requestCtx.get()).getRequest();
        } else {
            return null;
        }
    }

    public ServletResponse getServletResponse() {
        if (requestCtx.get() != null) {
            return requestCtx.get().getResponse();
        } else {
            return null;
        }
    }

    public HttpServletResponse getHttpServletResponse() {
        if (requestCtx.get() instanceof HttpServletRequestContext) {
            return HttpServletRequestContext.class.cast(requestCtx.get()).getResponse();
        } else {
            return null;
        }
    }

    public HttpSession getHttpSession() {
        if (requestCtx.get() instanceof HttpServletRequestContext) {
            return HttpServletRequestContext.class.cast(requestCtx.get()).getRequest().getSession();
        } else {
            return null;
        }
    }

    static class InternalServletContextEvent {
        private ServletContext ctx;

        InternalServletContextEvent(ServletContext ctx) {
            this.ctx = ctx;
        }

        public ServletContext getServletContext() {
            return ctx;
        }
    }

    static class InternalServletRequestEvent {
        private ServletRequest request;

        InternalServletRequestEvent(ServletRequest request) {
            this.request = request;
        }

        public ServletRequest getServletRequest() {
            return request;
        }
    }

    static class InternalServletResponseEvent {
        private ServletResponse response;

        InternalServletResponseEvent(ServletResponse response) {
            this.response = response;
        }

        public ServletResponse getServletResponse() {
            return response;
        }
    }

    static class InternalHttpSessionEvent {
        private HttpSession session;

        InternalHttpSessionEvent(HttpSession session) {
            this.session = session;
        }

        public HttpSession getHttpSession() {
            return session;
        }
    }
}
