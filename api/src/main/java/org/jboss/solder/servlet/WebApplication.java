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
package org.jboss.solder.servlet;

import javax.servlet.ServletContext;

/**
 * Information about the current web application. This object can be used to observe the startup and shutdown events without
 * tying to the servlet API.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public class WebApplication {
    private final String name;
    private final String contextPath;
    private final String serverInfo;
    private final long startTime;

    public WebApplication(ServletContext ctx) {
        this(ctx.getServletContextName(), ctx.getContextPath(), ctx.getServerInfo());
    }

    public WebApplication(String name, String contextPath, String serverInfo) {
        this.name = name;
        this.contextPath = contextPath;
        this.serverInfo = serverInfo;
        this.startTime = System.currentTimeMillis();
    }

    public String getName() {
        return name;
    }

    public String getContextPath() {
        return contextPath;
    }

    public String getServerInfo() {
        return serverInfo;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getRunningTime() {
        return System.currentTimeMillis() - startTime;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((contextPath == null) ? 0 : contextPath.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((serverInfo == null) ? 0 : serverInfo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof WebApplication)) {
            return false;
        }

        WebApplication other = (WebApplication) obj;
        if (contextPath == null) {
            if (other.contextPath != null) {
                return false;
            }
        } else if (!contextPath.equals(other.contextPath)) {
            return false;
        }

        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }

        if (serverInfo == null) {
            if (other.serverInfo != null) {
                return false;
            }
        } else if (!serverInfo.equals(other.serverInfo)) {
            return false;
        }

        return true;
    }
}
