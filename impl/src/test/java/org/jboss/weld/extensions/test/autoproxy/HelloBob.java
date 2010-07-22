package org.jboss.weld.extensions.test.autoproxy;

import javax.enterprise.inject.Default;

@Default
public interface HelloBob extends HelloService
{
   @PersonName("Bob")
   public String sayHello();
}
