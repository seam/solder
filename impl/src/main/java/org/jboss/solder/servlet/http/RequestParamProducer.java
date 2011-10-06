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
import javax.servlet.http.HttpServletRequest;

import org.jboss.solder.core.Requires;
import org.jboss.solder.reflection.PrimitiveTypes;
import org.jboss.solder.servlet.ServletExtension;

/**
 * A producer for a String bean qualified &#064;{@link RequestParam}.
 * <p/>
 * <p>
 * Provides a producer method that retrieves the value of the specified request parameter from
 * {@link HttpServletRequest#getParameter(String)} and makes it available as a dependent-scoped bean of type String qualified
 * &#064;RequestParam. The name of the request parameter to lookup is either the value of the &#064;RequestParam annotation or,
 * if the annotation value is empty, the name of the injection point (e.g., the field name).
 * </p>
 * <p>
 * If the request parameter is not present, and the injection point is annotated with &#064;DefaultValue, the value of the
 * &#064;DefaultValue annotation is returned instead. If &#064;DefaultValue is not present, <code>null</code> is returned.
 * </p>
 *
 * @author Nicklas Karlsson
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 * @see RequestParam
 * @see DefaultValue
 */
@Requires("javax.servlet.Servlet")
public class RequestParamProducer {
    @Inject
    private HttpServletRequest request;

    @Produces
    @TypedParamValue
    // FIXME find better place to cache the valueOf methods
    // TODO support collection values
    protected Object getTypedParamValue(InjectionPoint ip, ServletExtension ext) {
        String v = getParameterValue(getParameterName(ip), ip);
        Class<?> t = PrimitiveTypes.box(resolveExpectedType(ip));
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

    private String getParameterName(InjectionPoint ip) {
        String parameterName = ip.getAnnotated().getAnnotation(RequestParam.class).value();
        if ("".equals(parameterName)) {
            parameterName = ip.getMember().getName();
        }
        return parameterName;
    }

    private String getParameterValue(String parameterName, InjectionPoint ip) {
        return isParameterInRequest(parameterName) ? request.getParameter(parameterName) : getDefaultValue(ip);
    }

    private boolean isParameterInRequest(String parameterName) {
        return request.getParameterMap().containsKey(parameterName);
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
