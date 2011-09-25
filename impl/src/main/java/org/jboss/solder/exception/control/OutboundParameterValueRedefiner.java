/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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

package org.jboss.solder.exception.control;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

import org.jboss.solder.reflection.annotated.ParameterValueRedefiner;
import org.jboss.solder.exception.control.CaughtException;

/**
 * Redefiner allowing to inject a non contextual instance of {@link CaughtException} into the first parameter. This
 * class is immutable.
 */
public class OutboundParameterValueRedefiner implements ParameterValueRedefiner {
    final private CaughtException<?> event;
    final private BeanManager bm;
    final private Bean<?> declaringBean;
    final private HandlerMethodImpl<?> handlerMethod;

    /**
     * Sole constructor.
     *
     * @param event   instance of CaughtException to inject.
     * @param manager active BeanManager
     * @param handler Handler method this redefiner is for
     */
    public OutboundParameterValueRedefiner(final CaughtException<?> event, final BeanManager manager,
                                           final HandlerMethodImpl<?> handler) {
        this.event = event;
        this.bm = manager;
        this.declaringBean = handler.getBean(manager);
        this.handlerMethod = handler;
    }

    /**
     * {@inheritDoc}
     */
    public Object redefineParameterValue(ParameterValue value) {
        CreationalContext<?> ctx = this.bm.createCreationalContext(this.declaringBean);

        try {
            if (value.getPosition() == this.handlerMethod.getHandlerParameter().getPosition()) {
                return event;
            }
            return value.getDefaultValue(ctx);
        } finally {
            if (ctx != null) {
                ctx.release();
            }
        }
    }
}
