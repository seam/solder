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

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.jboss.solder.core.Requires;
import org.jboss.solder.reflection.PrimitiveTypes;
import org.jboss.solder.servlet.ServletExtension;

/**
 * A producer for a String bean qualified &#064;{@link CookieParam}.
 * <p/>
 * <p>
 * Provides a producer method that retrieves the value of the specified cookie from {@link HttpServletRequest#getCookies()}
 * array and makes it available as a dependent-scoped bean of type String qualified &#064;CookieParam. The name of the cookie to
 * lookup is either the value of the &#064;CookieParam annotation or, if the annotation value is empty, the name of the
 * injection point (e.g., the field name).
 * </p>
 * <p>
 * If the cookie is not present, and the injection point is annotated with &#064;DefaultValue, the value of the
 * &#064;DefaultValue annotation is returned instead. If &#064;DefaultValue is not present, <code>null</code> is returned.
 * </p>
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 * @see CookieParam
 * @see DefaultValue
 */
@Requires("javax.servlet.Servlet")
public class CookieParamProducer {
    @Inject
    private HttpServletRequest request;

    @Produces
    @TypedParamValue
    // FIXME find better place to cache the valueOf methods
    protected Object getTypedParamValue(InjectionPoint ip, ServletExtension ext) {
        String cookieName = getCookieName(ip);
        Class<?> t = PrimitiveTypes.box(resolveExpectedType(ip));
        if (t.equals(Cookie.class)) {
            return getCookie(cookieName, ip);
        }

        String v = getCookieValue(cookieName, ip);
        if (t.equals(String.class)) {
            return v;
        }
        try {
            Member converter = ext.getConverterMember(t);
            return converter instanceof Constructor ? ((Constructor<?>) converter).newInstance(v) : ((Method) converter)
                    .invoke(null, v);
        }
        // TODO should at least debug we couldn't convert the value
        catch (Exception e) {
        }
        return null;
    }

    private String getCookieName(InjectionPoint ip) {
        String headerName = ip.getAnnotated().getAnnotation(CookieParam.class).value();
        if ("".equals(headerName)) {
            headerName = ip.getMember().getName();
        }
        return headerName;
    }

    private Cookie getCookie(String cookieName, InjectionPoint ip) {
        Cookie cookie = null;
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals(cookieName)) {
                    cookie = c;
                    break;
                }
            }
        }

        if (cookie == null) {
            String defaultValue = getDefaultValue(ip);
            if (defaultValue != null) {
                cookie = new Cookie(cookieName, defaultValue);
            }
        }

        return cookie;
    }

    private String getCookieValue(String cookieName, InjectionPoint ip) {
        // do we have to do any specific filtering here?
        if (request.getCookies() != null) {
            for (Cookie c : request.getCookies()) {
                if (c.getName().equals(cookieName)) {
                    return c.getValue();
                }
            }
        }
        return getDefaultValue(ip);
    }

    private String getDefaultValue(InjectionPoint ip) {
        DefaultValue defaultValueAnnotation = ip.getAnnotated().getAnnotation(DefaultValue.class);
        return defaultValueAnnotation == null ? null : defaultValueAnnotation.value();
    }

    private Class<?> resolveExpectedType(final InjectionPoint ip) {
        Type t = ip.getType();
        if (t instanceof ParameterizedType && ((ParameterizedType) t).getActualTypeArguments().length == 1) {
            return (Class<?>) ((ParameterizedType) t).getActualTypeArguments()[0];
        } else if (t instanceof Class) {
            return (Class<?>) t;
        } else {
            return Object.class;
        }
    }
}
