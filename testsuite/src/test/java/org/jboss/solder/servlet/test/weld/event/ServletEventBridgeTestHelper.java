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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.solder.servlet.ServletRequestContext;
import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.Destroyed;
import org.jboss.solder.servlet.event.DidActivate;
import org.jboss.solder.servlet.event.Initialized;
import org.jboss.solder.servlet.event.Path;
import org.jboss.solder.servlet.event.Started;
import org.jboss.solder.servlet.event.WillPassivate;
import org.jboss.solder.servlet.http.HttpServletRequestContext;
import org.junit.Assert;

/**
 * @author Nicklas Karlsson
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@ApplicationScoped
public class ServletEventBridgeTestHelper {
    private Map<String, List<Object>> observations = new HashMap<String, List<Object>>();

    private void recordObservation(String id, Object observation) {
        List<Object> observed = observations.get(id);
        if (observed == null) {
            observed = new ArrayList<Object>();
            observations.put(id, observed);
        }
        observed.add(observation);
    }

    public Map<String, List<Object>> getObservations() {
        return observations;
    }

    public void reset() {
        observations.clear();
    }

    public void assertObservations(String id, Object... observations) {
        List<Object> observed = this.observations.get(id);

        if (observations.length > 0) {
            Assert.assertNotNull("Observer [@Observes " + id + "] was never notified", observed);
        } else {
            Assert.assertEquals("Observer [@Observes " + id + "] should not have been called", 0,
                    (observed != null ? observed.size() : 0));
            return;
        }

        for (Object o : observations) {
            if (!observed.remove(o)) {
                Assert.fail("Observer [@Observes " + id + "] notified too few times; expected payload: " + o);
            }
        }

        if (observed.size() > 0) {
            Assert.fail("Observer [@Observes " + id + "] notified too many times; extra payload: " + observed);
        }
    }

    public void observeServletRequest(@Observes ServletRequest req) {
        recordObservation("ServletRequest", req);
    }

    public void observeServletRequestInitialized(@Observes @Initialized ServletRequest req) {
        recordObservation("@Initialized ServletRequest", req);
    }

    public void observeServletRequestDestroyed(@Observes @Destroyed ServletRequest req) {
        recordObservation("@Destroyed ServletRequest", req);
    }

    public void observeWebApplication(@Observes WebApplication webapp) {
        recordObservation("WebApplication", webapp);
    }

    public void observeWebApplicationInitialized(@Observes @Initialized WebApplication webapp) {
        recordObservation("@Initialized WebApplication", webapp);
    }

    public void observeWebApplicationStarted(@Observes @Started WebApplication webapp) {
        recordObservation("@Started WebApplication", webapp);
    }

    public void observeWebApplicationDestroyed(@Observes @Destroyed WebApplication webapp) {
        recordObservation("@Destroyed WebApplication", webapp);
    }

    public void observeHttpServletRequest(@Observes HttpServletRequest req) {
        recordObservation("HttpServletRequest", req);
    }

    public void observeHttpServletRequestInitialized(@Observes @Initialized HttpServletRequest req) {
        recordObservation("@Initialized HttpServletRequest", req);
    }

    public void observeHttpServletRequestDestroyed(@Observes @Destroyed HttpServletRequest req) {
        recordObservation("@Destroyed HttpServletRequest", req);
    }

    public void observeServletResponse(@Observes ServletResponse res) {
        recordObservation("ServletResponse", res);
    }

    public void observeServletResponseInitialized(@Observes @Initialized ServletResponse res) {
        recordObservation("@Initialized ServletResponse", res);
    }

    public void observeServletResponseDestroyed(@Observes @Destroyed ServletResponse res) {
        recordObservation("@Destroyed ServletResponse", res);
    }

    public void observeHttpServletResponse(@Observes HttpServletResponse res) {
        recordObservation("HttpServletResponse", res);
    }

    public void observeHttpServletResponseInitialized(@Observes @Initialized HttpServletResponse res) {
        recordObservation("@Initialized HttpServletResponse", res);
    }

    public void observeHttpServletResponseDestroyed(@Observes @Destroyed HttpServletResponse res) {
        recordObservation("@Destroyed HttpServletResponse", res);
    }

    public void observeServletRequestContext(@Observes ServletRequestContext ctx) {
        recordObservation("ServletRequestContext", ctx);
    }

    public void observeServletRequestContextInitialized(@Observes @Initialized ServletRequestContext ctx) {
        recordObservation("@Initialized ServletRequestContext", ctx);
    }

    public void observeServletRequestContextDestroyed(@Observes @Destroyed ServletRequestContext ctx) {
        recordObservation("@Destroyed ServletRequestContext", ctx);
    }

    public void observeHttpServletRequestContext(@Observes HttpServletRequestContext ctx) {
        recordObservation("HttpServletRequestContext", ctx);
    }

    public void observeHttpServletRequestContextInitialized(@Observes @Initialized HttpServletRequestContext ctx) {
        recordObservation("@Initialized HttpServletRequestContext", ctx);
    }

    public void observeHttpServletRequestContextDestroyed(@Observes @Destroyed HttpServletRequestContext ctx) {
        recordObservation("@Destroyed HttpServletRequestContext", ctx);
    }

    public void observeHttpSession(@Observes HttpSession sess) {
        recordObservation("HttpSession", sess);
    }

    public void observeHttpSessionDidActivate(@Observes @DidActivate HttpSession sess) {
        recordObservation("@DidActivate HttpSession", sess);
    }

    public void observeSessionWillPassivate(@Observes @WillPassivate HttpSession sess) {
        recordObservation("@WillPassivate HttpSession", sess);
    }

    public void observeSessionInitialized(@Observes @Initialized HttpSession sess) {
        recordObservation("@Initialized HttpSession", sess);
    }

    public void observeSessionDestroyed(@Observes @Destroyed HttpSession sess) {
        recordObservation("@Destroyed HttpSession", sess);
    }

    public void observeServletContext(@Observes ServletContext ctx) {
        recordObservation("ServletContext", ctx);
    }

    public void observeServletContextInitialized(@Observes @Initialized ServletContext ctx) {
        recordObservation("@Initialized ServletContext", ctx);
    }

    public void observeServletContextDestroyed(@Observes @Destroyed ServletContext ctx) {
        recordObservation("@Destroyed ServletContext", ctx);
    }

    public void observeHttpServletRequestInitializedForPathA(@Observes @Initialized @Path("pathA") HttpServletRequest req) {
        recordObservation("@Initialized @Path(\"pathA\") HttpServletRequest", req);
    }

    public void observeHttpServletRequestInitializedForPathB(@Observes @Initialized @Path("pathB") HttpServletRequest req) {
        recordObservation("@Initialized @Path(\"pathB\") HttpServletRequest", req);
    }

    public static class NoOpFilterChain implements FilterChain {
        public static final FilterChain INSTANCE = new NoOpFilterChain();

        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        }
    }
}
