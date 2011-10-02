package org.jboss.solder.servlet.http;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jboss.solder.core.Veto;
import org.jboss.solder.servlet.ServletRequestContext;

/**
 * Encapsulates the {@link HttpServletRequest} and {@link HttpServletResponse} for the current request and dually provides
 * access to the {@link ServletContext} and {@link HttpSession}, which are accessed from the {@link HttpServletRequest} object.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Veto
public class HttpServletRequestContext extends ServletRequestContext {
    // required for scoped producer
    public HttpServletRequestContext() {
    }

    public HttpServletRequestContext(ServletRequest request, ServletResponse response) {
        super(enforceType(request, HttpServletRequest.class), enforceType(response, HttpServletResponse.class));
    }

    public HttpServletRequestContext(ServletRequest request) {
        super(enforceType(request, HttpServletRequest.class), null);
    }

    public HttpServletRequestContext(HttpServletRequest request, HttpServletResponse response) {
        super(request, response);
    }

    public HttpServletRequestContext(HttpServletRequest request) {
        super(request, null);
    }

    @Override
    public HttpServletRequest getRequest() {
        return (HttpServletRequest) super.getRequest();
    }

    @Override
    public HttpServletResponse getResponse() {
        return (HttpServletResponse) super.getResponse();
    }

    public HttpSession getHttpSession() {
        return getRequest().getSession();
    }

    public String getContextPath() {
        return getRequest().getContextPath();
    }

    private static <T> T enforceType(Object arg, Class<T> expectedType) {
        if (!expectedType.isAssignableFrom(arg.getClass())) {
            throw new IllegalArgumentException("Type " + expectedType.getSimpleName() + " expected, but got " + arg);
        }
        return expectedType.cast(arg);
    }
}
