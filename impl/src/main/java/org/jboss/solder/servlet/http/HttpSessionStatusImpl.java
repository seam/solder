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

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jboss.solder.core.Requires;

/**
 * A helper bean that can be injected to check the status of the HttpSession and acquiring it
 *
 * @author Nicklas Karlsson
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 * @author Shane Bryzak
 */
@RequestScoped
@Requires("javax.servlet.Servlet")
public class HttpSessionStatusImpl implements HttpSessionStatus {
    @Inject
    private Instance<HttpServletRequest> request;

    /**
     * Checks whether there is an active HttpSession associated with the current request.
     *
     * @return Whether a valid session is associated with this request
     */
    public boolean isActive() {
        try {
            if (!request.get().isRequestedSessionIdValid()) {
                return false;
            }
    
            return request.get().getSession(false) != null;
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    /**
     * Returns the current HttpSession associated with this request. If a session is not associated with the current request, a
     * new session is first initialized.
     *
     * @return HttpSession The existing session, or a new session if one has not yet been created
     */
    public HttpSession get() {
        return request.get().getSession();
    }
}
