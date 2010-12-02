package org.jboss.seam.solder.properties;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Member;
import java.lang.reflect.Type;

/**
 * A representation of a JavaBean style property
 * 
 * @see Properties
 * 
 * @author Pete Muir
 * @author Shane Bryzak
 *
 * @param <V> the type of the properties value
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
   public String getName();
   
   /**
    * Returns the property type
    * 
    * @return The property type
    */
   public Type getBaseType();
   
   /**
    * Returns the property type
    * 
    * @return The property type
    */
   public Class<V> getJavaClass();   
   
   /**
    * Get the element responsible for retrieving the property value
    * 
    * @return
    */
   public AnnotatedElement getAnnotatedElement();
   
   /**
    * Get the member responsible for retrieving the property value
    * 
    * @return
    */
   public Member getMember();
   
   /**
    * Returns the property value for the specified bean. The property to be
    * returned is either a field or getter method.
    * 
    * @param bean The bean to read the property from
    * @return The property value
    * @throws ClassCastException if the value is not of the type V
    */
   public V getValue(Object instance);
   
   /**
    * This method sets the property value for a specified bean to the specified
    * value. The property to be set is either a field or setter method.
    * 
    * @param bean The bean containing the property to set
    * @param value The new property value
    */
   public void setValue(Object instance, V value);   
   
   /**
    * Returns the class that declares the property
    * 
    * @return
    */
   public Class<?> getDeclaringClass();
   
   /**
    * Indicates whether this is a read-only property
    * 
    * @return
    */
   boolean isReadOnly();
}