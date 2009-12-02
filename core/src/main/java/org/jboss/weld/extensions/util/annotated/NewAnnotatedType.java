package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * AnnotatedType implementation for adding beans in the BeforeBeanDiscovery
 * event
 * 
 * @author Stuart Douglas
 * 
 */
public class NewAnnotatedType<X> extends AbstractNewAnnotatedElement implements AnnotatedType<X>
{

   private final Set<NewAnnotatedConstructor<X>> constructors = new HashSet<NewAnnotatedConstructor<X>>();
   private final Set<NewAnnotatedField<? super X>> fields = new HashSet<NewAnnotatedField<? super X>>();
   private final Set<NewAnnotatedMethod<? super X>> methods = new HashSet<NewAnnotatedMethod<? super X>>();

   // maps fields to the field objects
   private final Map<Field, NewAnnotatedField<X>> fieldMap = new HashMap<Field, NewAnnotatedField<X>>();
   // maps method names to the method objects
   private final Map<Method, NewAnnotatedMethod<X>> methodMap = new HashMap<Method, NewAnnotatedMethod<X>>();

   private final Class<X> javaClass;

   public NewAnnotatedType(Class<X> clazz, boolean readAnnotations)
   {
      super(clazz, readAnnotations);
      javaClass = clazz;
      for (Constructor<?> c : clazz.getConstructors())
      {
         constructors.add(new NewAnnotatedConstructor<X>(this, c, readAnnotations));
      }
      for (Method m : clazz.getMethods())
      {
         NewAnnotatedMethod<X> met = new NewAnnotatedMethod<X>(this, m, readAnnotations);
         methods.add(met);
         methodMap.put(m, met);
      }
      for (Field f : clazz.getFields())
      {
         NewAnnotatedField<X> b = new NewAnnotatedField<X>(this, f, readAnnotations);
         fields.add(b);
         fieldMap.put(f, b);
      }

   }

   /**
    * clears all existing annotation data from a type
    */
   @Override
   public void clearAllAnnotations()
   {
      super.clearAllAnnotations();
      for (AbstractNewAnnotatedElement c : constructors)
      {
         c.clearAllAnnotations();
      }
      for (AbstractNewAnnotatedElement c : fields)
      {
         c.clearAllAnnotations();
      }
      for (AbstractNewAnnotatedElement c : methods)
      {
         c.clearAllAnnotations();
      }
   }

   public Set<AnnotatedConstructor<X>> getConstructors()
   {
      return (Set) Collections.unmodifiableSet(constructors);
   }

   public Set<AnnotatedField<? super X>> getFields()
   {
      return (Set) Collections.unmodifiableSet(fields);
   }

   public Class<X> getJavaClass()
   {
      return javaClass;
   }

   public Set<AnnotatedMethod<? super X>> getMethods()
   {
      return (Set) Collections.unmodifiableSet(methods);
   }

   public NewAnnotatedField<X> getField(Field field)
   {
      return fieldMap.get(field);
   }

   public NewAnnotatedMethod<X> getMethod(Method m)
   {
      return methodMap.get(m);
   }

}
