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
package org.jboss.solder.servlet.test.weld.http;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.enterprise.inject.Typed;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.solder.servlet.http.CookieParam;
import org.jboss.solder.servlet.http.DefaultValue;
import org.jboss.solder.servlet.http.HeaderParam;
import org.jboss.solder.servlet.http.ImplicitHttpServletObjectsProducer;
import org.jboss.solder.servlet.http.RedirectBuilder;
import org.jboss.solder.servlet.http.RedirectBuilderImpl;
import org.jboss.solder.servlet.http.RequestParam;
import org.jboss.solder.servlet.test.weld.util.Deployments;
import org.jboss.shrinkwrap.api.Archive;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@RunWith(Arquillian.class)
// TODO split up into individual tests for each param type
public class RequestParamProducerTest {
    private static final String IMPLICIT_PARAM = "implicit";
    private static final String EXPLICIT_PARAM = "explicit";
    private static final String MISSING_PARAM = "missing";
    private static final String IMPLICIT_VALUE = IMPLICIT_PARAM + "Value";
    private static final String EXPLICIT_VALUE = EXPLICIT_PARAM + "Value";
    private static final String DEFAULT_VALUE = "defaultValue";

    @Deployment
    public static Archive<?> createDeployment() {
        return Deployments
                .createMockableBeanWebArchive(Deployments.exclude(ImplicitHttpServletObjectsProducer.class, RedirectBuilder.class, RedirectBuilderImpl.class))
                .addClasses(RequestParamProducerTest.class, Suit.class);
    }

    @Inject
    @RequestParam(EXPLICIT_PARAM)
    Instance<String> explicit;

    @Inject
    @RequestParam
    Instance<String> implicit;

    @Inject
    @RequestParam(MISSING_PARAM)
    @DefaultValue(DEFAULT_VALUE)
    Instance<String> missing;

    @Inject
    @RequestParam(MISSING_PARAM)
    Instance<String> missingNoDefault;

    @Inject
    @RequestParam("pageSize")
    Instance<Integer> pageSize;

    @Inject
    @RequestParam("suit")
    Instance<Suit> suit;

    @Inject
    @RequestParam("airDate")
    Instance<Date> airDate;

    @Inject
    @HeaderParam("Cache-Control")
    Instance<String> cacheControl;

    @Inject
    @CookieParam
    Instance<String> chocolate;

    @Inject
    @CookieParam("chocolate")
    Instance<Cookie> chocolateCookie;

    @Test
    public void should_inject_value_for_explicit_http_param() {
        Assert.assertEquals(EXPLICIT_VALUE, explicit.get());
    }

    @Test
    public void should_inject_value_for_implicit_http_param() {
        Assert.assertEquals(IMPLICIT_VALUE, implicit.get());
    }

    @Test
    public void should_inject_default_value_for_missing_http_param() {
        Assert.assertEquals(DEFAULT_VALUE, missing.get());
    }

    @Test
    public void should_inject_null_value_for_missing_http_param() {
        Assert.assertNull(missingNoDefault.get());
    }

    @Test
    public void should_inject_value_for_typed_http_param(@RequestParam("page") int page) {
        Assert.assertEquals((Integer) 25, pageSize.get());
        Assert.assertEquals((Integer) 1, (Integer) page);
    }

    @Test
    public void should_inject_value_for_enum_http_param() {
        Assert.assertEquals(Suit.DIAMONDS, suit.get());
    }

    @Test
    public void should_inject_value_for_date_http_param() {
        Calendar cal = Calendar.getInstance();
        cal.set(2010, 7, 1, 20, 0, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date value = airDate.get();
        Assert.assertNotNull(value);
        Assert.assertEquals(cal.getTime().getTime(), value.getTime());
    }

    @Test
    public void should_inject_value_for_header_param() {
        Assert.assertEquals("no-cache", cacheControl.get());
    }

    @Test
    public void should_inject_value_for_cookie_param() {
        Assert.assertEquals("chip", chocolate.get());
    }

    @Test
    public void should_inject_cookie_for_cookie_param() {
        Cookie c = chocolateCookie.get();
        Assert.assertNotNull(c);
        Assert.assertEquals("chip", c.getValue());
    }

    @Produces
    @Typed(HttpServletRequest.class)
    protected HttpServletRequest getHttpServletRequest() {
        HttpServletRequest req = mock(HttpServletRequest.class);

        Map<String, String[]> parameters = new HashMap<String, String[]>();
        parameters.put(IMPLICIT_PARAM, new String[]{IMPLICIT_VALUE});
        parameters.put(EXPLICIT_PARAM, new String[]{EXPLICIT_VALUE});
        parameters.put("page", new String[]{"1"});
        parameters.put("pageSize", new String[]{"25"});
        parameters.put("suit", new String[]{Suit.DIAMONDS.name()});
        parameters.put("airDate", new String[]{"2010-08-01 20:00"});
        when(req.getParameterMap()).thenReturn(parameters);
        when(req.getParameter(IMPLICIT_PARAM)).thenReturn(IMPLICIT_VALUE);
        when(req.getParameter(EXPLICIT_PARAM)).thenReturn(EXPLICIT_VALUE);
        when(req.getParameter("page")).thenReturn("1");
        when(req.getParameter("pageSize")).thenReturn("25");
        when(req.getParameter("suit")).thenReturn(Suit.DIAMONDS.name());
        when(req.getParameter("airDate")).thenReturn("2010-08-01 20:00");

        Vector<String> headerNames = new Vector<String>();
        headerNames.add("Cache-Control");
        when(req.getHeaderNames()).thenReturn(headerNames.elements());
        when(req.getHeader("Cache-Control")).thenReturn("no-cache");

        Cookie[] cookies = new Cookie[]{new Cookie("chocolate", "chip")};
        when(req.getCookies()).thenReturn(cookies);

        return req;
    }
}
