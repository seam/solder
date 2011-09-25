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

import javax.servlet.http.Cookie;

/**
 * A builder, similar in style to the ResponseBuilder from JAX-RS, that simplifies the task of sending a redirect URL to the
 * client.
 * <p/>
 * <strong>This is a proposed API and is subject to change</strong>
 *
 * @author <a href="http://community.jboss.org/people/dan.j.allen">Dan Allen</a>
 */
public interface RedirectBuilder {
    void send();

    RedirectBuilder redirect();

    RedirectBuilder redirect(String path);

    RedirectBuilder temporaryRedirect(String path);

    RedirectBuilder seeOther(String path);

    RedirectBuilder cookie(Cookie... cookies);

    RedirectBuilder param(String name, Object... values);

    RedirectBuilder param(boolean replace, String name, Object... values);

    RedirectBuilder header(String name, Object... values);

    RedirectBuilder header(boolean replace, String name, Object... values);

    RedirectBuilder fragment(String fragment);

    RedirectBuilder mirror();

    RedirectBuilder encodeSessionId();
}
