package org.jboss.weld.extensions.util.annotated;

import java.lang.reflect.Constructor;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.inject.spi.AnnotatedCallable;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;

/**
 * 
 * @author Stuart Douglas
 * 
 */
public abstract class AbstractNewAnnotatedCallable<X, Y extends Member> extends AbstractNewAnnotatedMember<X, Y> implements AnnotatedCallable<X>
{

   private final List<NewAnnotatedParameter<X>> parameters = new ArrayList<NewAnnotatedParameter<X>>();

   protected AbstractNewAnnotatedCallable(AnnotatedType<X> type, Method method, boolean readAnnotations)
   {
      super(type, (Y) method, method.getReturnType(), readAnnotations);
      int len = method.getTypeParameters().length;
      for (int i = 0; i < len; ++i)
      {
         NewAnnotatedParameter<X> p = new NewAnnotatedParameter<X>(this, method.getParameterTypes()[i], i, readAnnotations);
         parameters.add(p);
      }
   }

   protected AbstractNewAnnotatedCallable(AnnotatedType<X> type, Constructor<X> constructor, boolean readAnnotations)
   {
      super(type, (Y) constructor, constructor.getDeclaringClass(), readAnnotations);
      int len = constructor.getTypeParameters().length;
      for (int i = 0; i < len; ++i)
      {
         NewAnnotatedParameter<X> p = new NewAnnotatedParameter<X>(this, constructor.getParameterTypes()[i], i, readAnnotations);
         parameters.add(p);
      }
   }
   

   @Override
   public void clearAllAnnotations()
   {
      super.clearAllAnnotations();
      for (NewAnnotatedParameter<X> p : parameters)
      {
         p.clearAllAnnotations();
      }
   }

   public List<AnnotatedParameter<X>> getParameters()
   {
      return (List) Collections.unmodifiableList(parameters);
   }

   public List<NewAnnotatedParameter<X>> getNewAnnotatedParameters()
   {
      return Collections.unmodifiableList(parameters);
   }

   public NewAnnotatedParameter<X> getParameter(int index)
   {
      return parameters.get(index);

   }

}
