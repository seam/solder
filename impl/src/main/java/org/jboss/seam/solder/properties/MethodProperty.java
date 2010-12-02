package org.jboss.seam.solder.properties;

import java.lang.reflect.Method;


public interface MethodProperty<V> extends Property<V>
{

   public Method getAnnotatedElement();

}