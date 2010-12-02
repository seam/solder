package org.jboss.seam.solder.properties;

import java.lang.reflect.Field;


public interface FieldProperty<V> extends Property<V>
{

   public Field getAnnotatedElement();

}