package org.jboss.weld.extensions.util.properties.query;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Base interface for criteria used to locate bean properties
 * 
 * @author Shane Bryzak
 */
public interface PropertyCriteria
{
   /**
    * Tests whether the specified field matches the criteria
    * 
    * @param f
    * @return true if the field matches
    */
   boolean fieldMatches(Field f);
   
   /**
    * Tests whether the specified method matches the criteria
    * 
    * @param m
    * @return true if the method matches
    */
   boolean methodMatches(Method m);
}
