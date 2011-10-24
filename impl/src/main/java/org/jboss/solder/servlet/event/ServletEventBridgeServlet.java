package org.jboss.solder.servlet.event;

import java.io.IOException;
import java.lang.annotation.Annotation;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.solder.core.Requires;
import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.event.literal.DestroyedLiteral;
import org.jboss.solder.servlet.event.literal.StartedLiteral;

/**
 * Leverages the lifecycle of a Servlet to generate a CDI when the web application has been fully deployed and when it is
 * undeployed. This Servlet is assigned a high load-on-startup value (99999) in this module's web-fragment.xml to ensure it
 * comes last in the list of ordered Servlets.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
@Requires("javax.servlet.Servlet")
public class ServletEventBridgeServlet extends AbstractServletEventBridge implements Servlet {
    private transient ServletConfig config;

    public void init(ServletConfig config) throws ServletException {
        this.config = config;
        fireWebApplicationEvent(StartedLiteral.INSTANCE);
        // fireEvent(config.getServletContext(), StartedLiteral.INSTANCE);
    }

    public void destroy() {
        if (config != null) {
            fireWebApplicationEvent(DestroyedLiteral.INSTANCE);
            fireEvent(config.getServletContext(), DestroyedLiteral.INSTANCE);
        }
    }

    protected void fireWebApplicationEvent(Annotation qualifier) {
        if (config.getServletContext().getAttribute(WEB_APPLICATION_ATTRIBUTE_NAME) instanceof WebApplication) {
            fireEvent(config.getServletContext().getAttribute(WEB_APPLICATION_ATTRIBUTE_NAME), qualifier);
        }
    }

    public ServletConfig getServletConfig() {
        return config;
    }

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
    }

    public String getServletInfo() {
        return "Solder Servlet module WebApplication event publisher";
    }
}
