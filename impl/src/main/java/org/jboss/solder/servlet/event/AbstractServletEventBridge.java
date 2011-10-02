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
package org.jboss.solder.servlet.event;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import org.jboss.solder.logging.Logger;
import org.jboss.solder.beanManager.BeanManagerAware;
import org.jboss.solder.beanManager.BeanManagerUnavailableException;
import org.jboss.solder.servlet.WebApplication;
import org.jboss.solder.servlet.support.ServletLogger;

/**
 * An abstract class that handles sending events to the CDI event bus with support for environments where injection into the
 * Servlet component is not available.
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public abstract class AbstractServletEventBridge extends BeanManagerAware {
    private transient ServletLogger log = Logger.getMessageLogger(ServletLogger.class, ServletLogger.CATEGORY);

    public static String WEB_APPLICATION_ATTRIBUTE_NAME = WebApplication.class.getName();

    /**
     * Propagates the Servlet event to the CDI event bus if the BeanManager is available. If injection is available, this will
     * always be skipped, and thus the performance optimal
     */
    protected void fireEvent(final Object payload, final Annotation... qualifiers) {
        try {
            getBeanManager().fireEvent(payload, qualifiers);
        } catch (BeanManagerUnavailableException e) {
            log.skippingEventNoBeanManager(payload, Arrays.asList(qualifiers));
            return;
        }
    }
}
