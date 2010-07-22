package org.jboss.weld.extensions.test.autoproxy;

import javax.enterprise.inject.Default;

@Default
public interface HelloJane extends HelloService
{
   @PersonName("Jane")
   public String sayHello();
}
