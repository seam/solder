/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat, Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,  
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.weld.extensions.bean.generic;

import static org.jboss.weld.extensions.util.Reflections.getField;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.InjectionTarget;

import org.jboss.weld.extensions.bean.ForwardingInjectionTarget;

/**
 * injection target wrapper that injects the configuration for generic beans
 * 
 * @author Stuart Douglas <stuart@baileyroberts.com.au>
 * 
 * @param <T>
 */
public class GenericBeanInjectionTargetWrapper<T> extends ForwardingInjectionTarget<T>
{
   
   private static Set<Field> getFields(Class<?> clazz)
   {
      Set<Field> fields = new HashSet<Field>();
      fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
      Class<?> superClass = clazz.getSuperclass();
      if (superClass != Object.class)
      {
         fields.addAll(getFields(superClass));
      }
      return fields;
   }
   
   private final InjectionTarget<T> delegate;
   private final Annotation annotation;
   private final AnnotatedType<T> annotatedType;

   public GenericBeanInjectionTargetWrapper(AnnotatedType<T> annotatedType, InjectionTarget<T> delegate, Annotation annotation)
   {
      this.annotation = annotation;
      this.delegate = delegate;
      this.annotatedType = annotatedType;
   }
   
   @Override
   protected InjectionTarget<T> delegate()
   {
      return delegate;
   }

   @Override
   public void inject(T instance, CreationalContext<T> ctx)
   {
      for (Field f : getFields(instance.getClass()))
      {

         if (annotation.annotationType().isAssignableFrom(f.getType()))
         {
            AnnotatedField<? super T> annotatedField = getField(annotatedType, f);
            if (annotatedField.isAnnotationPresent(InjectConfiguration.class))
            {
               try
               {
                  f.setAccessible(true);
                  f.set(instance, annotation);
               }
               catch (IllegalAccessException e)
               {
                  throw new RuntimeException(e);
               }
            }
         }
      }

      delegate().inject(instance, ctx);
   }

}
