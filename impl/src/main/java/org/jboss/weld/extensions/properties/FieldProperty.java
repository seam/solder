package org.jboss.weld.extensions.properties;

import java.lang.reflect.Field;


public interface FieldProperty<V> extends Property<V>
{

   public Field getAnnotatedElement();

}