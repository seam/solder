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
package org.jboss.solder.servlet.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jboss.solder.core.Requires;

/**
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
// todo support switching to https/http
@Requires("javax.servlet.Servlet")
public class RedirectBuilderImpl implements RedirectBuilder {
    @Inject
    private HttpServletRequest request;

    @Inject
    private HttpServletResponse response;

    private String path = null;

    private String fragment = null;

    private Map<String, String[]> paramMap = new LinkedHashMap<String, String[]>();

    private Map<String, String[]> headerMap = new LinkedHashMap<String, String[]>();

    private List<Cookie> cookies = new ArrayList<Cookie>();

    private int status = HttpServletResponse.SC_MOVED_TEMPORARILY;

    private boolean encode = false;

    public RedirectBuilder mirror() {
        paramMap = new LinkedHashMap<String, String[]>(request.getParameterMap());
        headerMap = new LinkedHashMap<String, String[]>();
        for (Enumeration<String> names = request.getHeaderNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            List<String> v = new ArrayList<String>();
            for (Enumeration<String> values = request.getHeaders(name); values.hasMoreElements();) {
                v.add(values.nextElement());
            }
            headerMap.put(name, v.toArray(new String[0]));
        }
        path = request.getRequestURI();
        if (path.indexOf("#") >= 0) {
            fragment = path.substring(path.indexOf("#") + 1);
        }
        return this;
    }

    public RedirectBuilder header(String name, Object... values) {
        return header(true, name, values);
    }

    public RedirectBuilder header(boolean replace, String name, Object... values) {
        List<String> stringValues = new ArrayList<String>(values.length);
        if (!replace && headerMap.containsKey(name)) {
            for (String existing : headerMap.get(name)) {
                stringValues.add(existing);
            }
        }

        for (Object value : values) {
            if (value != null) {
                stringValues.add(value.toString());
            }
        }
        headerMap.put(name, stringValues.toArray(new String[0]));
        return this;
    }

    public RedirectBuilder param(String name, Object... values) {
        return param(true, name, values);
    }

    public RedirectBuilder param(boolean replace, String name, Object... values) {
        List<String> stringValues = new ArrayList<String>(values.length);
        if (!replace && paramMap.containsKey(name)) {
            for (String existing : paramMap.get(name)) {
                stringValues.add(existing);
            }
        }

        for (Object value : values) {
            if (value != null) {
                stringValues.add(value.toString());
            }
        }
        paramMap.put(name, stringValues.toArray(new String[0]));
        return this;
    }

    public RedirectBuilder cookie(Cookie... cookies) {
        for (Cookie c : cookies) {
            this.cookies.add(c);
        }
        return this;
    }

    public RedirectBuilder fragment(String fragment) {
        this.fragment = fragment;
        return this;
    }

    public RedirectBuilder seeOther(String path) {
        status = HttpServletResponse.SC_SEE_OTHER;
        this.path = path;
        return this;
    }

    public RedirectBuilder temporaryRedirect(String path) {
        status = HttpServletResponse.SC_TEMPORARY_REDIRECT;
        this.path = path;
        return this;
    }

    public RedirectBuilder redirect() {
        return redirect(null);
    }

    // TODO respect query string on path
    public RedirectBuilder redirect(String path) {
        status = HttpServletResponse.SC_MOVED_TEMPORARILY;
        this.path = path;
        return this;
    }

    public RedirectBuilder encodeSessionId() {
        encode = true;
        return this;
    }

    public void send() {
        if (response.isCommitted()) {
            throw new RuntimeException("Cannot issue redirect. Response already committed.");
        }

        String location = path;
        if (location == null) {
            location = request.getRequestURI();
        } else if (!location.startsWith("/")) {
            location = request.getContextPath() + location;
        }

        for (Entry<String, String[]> headers : headerMap.entrySet()) {
            String name = headers.getKey();
            for (String value : headers.getValue()) {
                response.addHeader(name, value);
            }
        }

        for (Cookie c : cookies) {
            response.addCookie(c);
        }

        String query = "";
        for (Entry<String, String[]> params : paramMap.entrySet()) {
            String name = params.getKey();
            for (String value : params.getValue()) {
                // FIXME naive
                query += (query.length() == 0 ? "?" : "&amp;") + name + "=" + value;
            }
        }

        location += query;

        if (fragment != null) {
            location += "#" + fragment;
        }

        try {
            response.resetBuffer();
            if (encode) {
                location = response.encodeRedirectURL(location);
            }
            response.setHeader("Location", location);
            response.setStatus(status);
            response.getWriter().flush();
            // more flexible to do it ourselves
            // response.sendRedirect(response.encodeRedirectURL(location));
        } catch (IOException e) {
            throw new RuntimeException("Failed to issue redirect. " + e.getMessage());
        }
    }
}
