package org.jboss.weld.extensions.util;

import java.beans.Introspector;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Base class for bean property wrappers
 * 
 * @author Shane Bryzak
 */
public abstract class AbstractBeanProperty
{
   /**
    * Property field
    */
   private Field propertyField;
   
   /**
    * Property getter method
    */
   private Method propertyGetter;
   
   /**
    * Property setter method
    */
   private Method propertySetter;
   
   /**
    * Property name
    */
   private String name;
   
   /**
    * The class containing the property
    */
   private Class<?> targetClass;
   
   /**
    * Flag indicating whether the target class has been scanned
    */
   private boolean scanned = false;   
   
   private boolean isFieldProperty;
   
   /**
    * Flag indicating whether a valid property has been found
    */
   private boolean valid = false;
   
   private Type propertyType;   
   
   /**
    * 
    * @param targetClass
    */
   public AbstractBeanProperty(Class<?> targetClass)
   {
      this.targetClass = targetClass;
   }
   
   protected void scan()
   {
      if (scanned) return;
      
      scanned = true;
      
      // First check declared fields
      for (Field f : targetClass.getDeclaredFields())
      {
         if (fieldMatches(f))
         {
            setupFieldProperty(f);            
            valid = true;
            return;
         }
      }      
      
      // Then check public fields, in case it's inherited
      for (Field f : targetClass.getFields())
      {
         if (fieldMatches(f)) 
         {
            setupFieldProperty(f);
            valid = true;
            return;
         }
      }
      
      // Then check public methods (we ignore private methods)
      for (Method m : targetClass.getMethods())
      {
         if (methodMatches(m))
         {
            String methodName = m.getName();
            
            if ( m.getName().startsWith("get") )
            {
               this.name = Introspector.decapitalize( m.getName().substring(3) );
            }
            else if ( methodName.startsWith("is") )
            {
               this.name = Introspector.decapitalize( m.getName().substring(2) );
            }            
            
            if (this.name != null)
            {
               this.propertyGetter = getGetterMethod(targetClass, this.name);
               this.propertySetter = getSetterMethod(targetClass, this.name);
               this.propertyType = this.propertyGetter.getGenericReturnType();
               isFieldProperty = false;               
               valid = true;
            }
            else
            {
               throw new IllegalStateException("Invalid accessor method, must start with 'get' or 'is'.  " +
                     "Method: " + m + " in class: " + targetClass);
            }
         }
      }        
   }
   
   protected abstract boolean fieldMatches(Field f);
   
   protected abstract boolean methodMatches(Method m);
      
   private void setupFieldProperty(Field propertyField)
   {
      this.propertyField = propertyField;
      isFieldProperty = true;
      this.name = propertyField.getName();
      this.propertyType = propertyField.getGenericType();
   }     
   
   /**
    * This method sets the property value for a specified bean to the specified 
    * value.  The property to be set is either a field or setter method that
    * matches the specified annotation class and returns true for the isMatch() 
    * method.
    * 
    * @param bean The bean containing the property to set
    * @param value The new property value
    * @throws Exception
    */
   public void setValue(Object bean, Object value) throws Exception
   {
      scan();
      
      if (isFieldProperty)
      {
         setFieldValue(propertyField, bean, value);        
      }
      else
      {
         invokeMethod(propertySetter, bean, value);
      }
   }
    
   /**
    * Returns the property value for the specified bean.  The property to be
    * returned is either a field or getter method that matches the specified
    * annotation class and returns true for the isMatch() method.
    * 
    * @param bean The bean to read the property from
    * @return The property value
    * @throws Exception
    */
   public Object getValue(Object bean) throws Exception
   {
      scan();
      
      if (isFieldProperty)
      {
         return getFieldValue(propertyField, bean);  
      }
      else
      {
         return invokeMethod(propertyGetter, bean);
      }
   }      
   
   /**
    * Returns the property type
    * 
    * @return The property type
    */
   public Type getPropertyType()
   {
      scan();
      return propertyType;
   }
   
   /**
    * Returns true if the property has been successfully located, otherwise
    * returns false.
    * 
    * @return
    */
   public boolean isValid()
   {
      scan();
      return valid;
   }      
   
   /**
    * Returns the name of the property. If the property is a field, then the
    * field name is returned.  Otherwise, if the property is a method, then the
    * name that is returned is the getter method name without the "get" or "is"
    * prefix, and a lower case first letter.
    * 
    * @return The name of the property
    */
   public String getName()
   {
      scan();      
      return name;
   }
   
   private Object getFieldValue(Field field, Object obj)
   {
      field.setAccessible(true);
      try
      {
         return field.get(obj);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(buildGetFieldValueErrorMessage(field, obj), e);        
      }      
      catch (NullPointerException ex)
      {         
         NullPointerException ex2 = new NullPointerException(
               buildGetFieldValueErrorMessage(field, obj));
         ex2.initCause(ex.getCause());
         throw ex2;
      }   
   }
   
   private String buildGetFieldValueErrorMessage(Field field, Object obj)
   {
      return String.format("Exception reading [%s] field from object [%s].",
            field.getName(), obj);
   }
   
   private void setFieldValue(Field field, Object obj, Object value)
   {
      field.setAccessible(true);
      try
      {
         field.set(obj, value);
      }
      catch (IllegalAccessException e)
      {
         throw new RuntimeException(buildSetFieldValueErrorMessage(field, obj, value), e);
      }
      catch (NullPointerException ex)
      {         
         NullPointerException ex2 = new NullPointerException(
               buildSetFieldValueErrorMessage(field, obj, value));
         ex2.initCause(ex.getCause());
         throw ex2;
      }
   }
   
   private String buildSetFieldValueErrorMessage(Field field, Object obj, Object value)
   {
      return String.format("Exception setting [%s] field on object [%s] to value [%s]",
            field.getName(), obj, value);
   }   
   
   private Object invokeMethod(Method method, Object obj, Object... args)
   {
      try
      {
         return method.invoke(obj, args);
      }
      catch (IllegalAccessException ex)
      {
         throw new RuntimeException(buildInvokeMethodErrorMessage(method, obj, args), ex);
      }
      catch (IllegalArgumentException ex)
      {
         throw new IllegalArgumentException(buildInvokeMethodErrorMessage(method, obj, args), ex.getCause()); 
      }
      catch (InvocationTargetException ex)
      {
         throw new RuntimeException(buildInvokeMethodErrorMessage(method, obj, args), ex);
      }
      catch (NullPointerException ex)
      {         
         NullPointerException ex2 = new NullPointerException(buildInvokeMethodErrorMessage(method, obj, args));
         ex2.initCause(ex.getCause());
         throw ex2;
      }
      catch (ExceptionInInitializerError e)
      {
         throw new RuntimeException(buildInvokeMethodErrorMessage(method, obj, args), e);
      }
   }   
   
   private String buildInvokeMethodErrorMessage(Method method, Object obj, Object... args)
   {
      StringBuilder message = new StringBuilder(String.format(
            "Exception invoking method [%s] on object [%s], using arguments [",
            method.getName(), obj));
      if (args != null) for (int i = 0; i < args.length; i++) message.append((i > 0 ? "," : "") + args[i]);
      message.append("]");
      return message.toString();
   }
   
   private Method getSetterMethod(Class<?> clazz, String name)
   {
      Method[] methods = clazz.getMethods();
      for (Method method: methods)
      {
         String methodName = method.getName();
         if ( methodName.startsWith("set") && method.getParameterTypes().length==1 )
         {
            if ( Introspector.decapitalize( methodName.substring(3) ).equals(name) )
            {
               return method;
            }
         }
      }
      throw new IllegalArgumentException("no such setter method: " + clazz.getName() + '.' + name);
   }
   
   private Method getGetterMethod(Class<?> clazz, String name)
   {
      Method[] methods = clazz.getMethods();
      for (Method method: methods)
      {
         String methodName = method.getName();
         if ( method.getParameterTypes().length==0 )
         {
            if ( methodName.startsWith("get") )
            {
               if ( Introspector.decapitalize( methodName.substring(3) ).equals(name) )
               {
                  return method;
               }
            }
            else if ( methodName.startsWith("is") )
            {
               if ( Introspector.decapitalize( methodName.substring(2) ).equals(name) )
               {
                  return method;
               }
            }
         }
      }
      throw new IllegalArgumentException("no such getter method: " + clazz.getName() + '.' + name);
   }     
}
