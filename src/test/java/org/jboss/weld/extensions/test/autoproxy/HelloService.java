package org.jboss.weld.extensions.test.autoproxy;

import org.jboss.weld.extensions.autoproxy.AutoProxy;

@AutoProxy(HelloServiceHandler.class)
public interface HelloService
{

}
