package org.jboss.solder.servlet;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.jboss.solder.core.Veto;

/**
 * Encapsulates the {@link ServletRequest} and {@link ServletResponse} for the current request and dually provides access to the
 * {@link ServletContext}, which is accessed from the {@link HttpServletRequest} object.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Veto
public class ServletRequestContext {
    private ServletRequest request;

    private ServletResponse response;

    // required for scoped producer
    public ServletRequestContext() {
    }

    public ServletRequestContext(ServletRequest request, ServletResponse response) {
        this.request = request;
        this.response = response;
    }

    public ServletRequestContext(ServletRequest request) {
        this(request, null);
    }

    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    public ServletRequest getRequest() {
        return request;
    }

    public ServletResponse getResponse() {
        return response;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((request == null) ? 0 : request.hashCode());
        result = prime * result + ((response == null) ? 0 : response.hashCode());
        return result;
    }

    /**
     * If the request and response on both objects map to the same Java identity, respectively, then consider these two objects
     * to be equivalent.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof ServletRequestContext)) {
            return false;
        }

        ServletRequestContext other = (ServletRequestContext) obj;
        if (request != other.request) {
            return false;
        }

        if (response != other.response) {
            return false;
        }

        return true;
    }
}
