package org.jboss.weld.extensions.util.properties;

import java.lang.reflect.Field;


public interface FieldProperty<V> extends Property<V>
{

   public Field getAnnotatedElement();

}