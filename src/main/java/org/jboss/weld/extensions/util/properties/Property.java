package org.jboss.weld.extensions.util.properties;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;

/**
 * A representation of a JavaBean style property
 * 
 * @see Properties
 * 
 * @author pmuir
 * @author Shane Bryzak
 *
 * @param <V>
 */
public interface Property<V>
{
   
   /**
    * Returns the name of the property. If the property is a field, then the
    * field name is returned. Otherwise, if the property is a method, then the
    * name that is returned is the getter method name without the "get" or "is"
    * prefix, and a lower case first letter.
    * 
    * @return The name of the property
    */
   String getName();
   
   /**
    * Returns the property type
    * 
    * @return The property type
    */
   Type getBaseType();
   
   /**
    * Returns the property type
    * 
    * @return The property type
    */
   Class<V> getJavaClass();   
      
   AnnotatedElement getAnnotatedElement();
   
   /**
    * Returns the property value for the specified bean. The property to be
    * returned is either a field or getter method.
    * 
    * @param bean The bean to read the property from
    * @return The property value
    */
    V getValue(Object instance);
   
   /**
    * This method sets the property value for a specified bean to the specified
    * value. The property to be set is either a field or setter method.
    * 
    * @param bean The bean containing the property to set
    * @param value The new property value
    */
   void setValue(Object instance, V value);   
}