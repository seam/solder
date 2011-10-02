/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc. and/or its affiliates, and individual
 * contributors by the @authors tag. See the copyright.txt in the
 * distribution for a full listing of individual contributors.
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
package org.jboss.solder.reflection;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.enterprise.inject.Stereotype;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.BeanManager;

/**
 * Inspect an {@link AnnotatedElement} or {@link Annotated} to obtain its meta-annotations and annotations,
 * featuring support for {@link Stereotype} annotations as a transitive annotation provider.
 *
 * @author Pete Muir
 * @author Dan Allen
 */
public class AnnotationInspector {
    private AnnotationInspector() {
    }

    /**
     * Discover if the {@link AnnotatedElement} has been annotated with the
     * specified annotation type. This method discovers annotations defined on
     * the element as well as annotations inherited from a CDI &#064;
     * {@link Stereotype} on the element.
     *
     * @param element        The element to inspect
     * @param annotationType The annotation type to expect
     * @param beanManager    The CDI BeanManager instance
     * @return <code>true</code> if annotation is present either on the element
     *         itself or one of its stereotypes, <code>false</code> if the
     *         annotation is not present
     */
    public static boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationType, BeanManager beanManager) {
        if (element.isAnnotationPresent(annotationType)) {
            return true;
        }

        return isAnnotationPresentOnStereotype(Arrays.asList(element.getAnnotations()), annotationType, beanManager);
    }

    /**
     * Discover if the {@link Annotated} has been annotated with the specified
     * annotation type. This method discovers annotations defined on
     * the element as well as annotations inherited from a CDI &#064;
     * {@link Stereotype} on the element.
     *
     * @param element        The element to inspect
     * @param annotationType The annotation type to expect
     * @param beanManager    The CDI BeanManager instance
     * @return <code>true</code> if annotation is present either on the element itself or one of its stereotypes,
     *         <code>false</code> if the annotation is not present
     */
    public static boolean isAnnotationPresent(Annotated annotated, Class<? extends Annotation> annotationType, BeanManager beanManager) {
        if (annotated.isAnnotationPresent(annotationType)) {
            return true;
        }

        return isAnnotationPresentOnStereotype(annotated.getAnnotations(), annotationType, beanManager);
    }

    /**
     * Discover if the {@link AnnotatedElement} has been annotated with the
     * specified annotation type. If the transitive argument is <code>true</code>
     * , this method also discovers annotations inherited from a CDI &#064;
     * {@link Stereotype} on the element.
     *
     * @param element        The element to inspect
     * @param annotationType The annotation to expect
     * @param transitive     Whether annotations provided by stereotypes should be
     *                       considered
     * @param beanManager    The CDI BeanManager instance
     * @return <code>true</code> if annotation is present on the element itself
     *         or (if specified) one of its stereotypes, <code>false</code> if the annotation is
     *         not present
     */
    public static boolean isAnnotationPresent(AnnotatedElement element, Class<? extends Annotation> annotationType, boolean transitive, BeanManager beanManager) {
        if (transitive) {
            return isAnnotationPresent(element, annotationType, beanManager);
        } else {
            return element.isAnnotationPresent(annotationType);
        }
    }

    /**
     * Discover if the {@link AnnotatedElement} has been annotated with a &#064;
     * {@link Stereotype} that provides the annotation type.
     *
     * @param element        The element to inspect
     * @param annotationType The annotation type to expect
     * @param beanManager    The CDI BeanManager instance
     * @return <code>true</code> if annotation is provided by a stereotype on the element,
     *         <code>false</code> if the annotation is not present
     */
    public static boolean isAnnotationPresentOnStereotype(AnnotatedElement element, Class<? extends Annotation> annotationType, BeanManager beanManager) {
        return isAnnotationPresentOnStereotype(Arrays.asList(element.getAnnotations()), annotationType, beanManager);
    }

    /**
     * Discover if the {@link Annotated} has been annotated with a &#064;
     * {@link Stereotype} that provides the specified annotation type.
     *
     * @param element        The element to inspect.
     * @param annotationType The annotation type to expect
     * @param beanManager    The CDI BeanManager instance
     * @return <code>true</code> if annotation is provided by a stereotype on the element,
     *         <code>false</code> if the annotation is not present
     */
    public static boolean isAnnotationPresentOnStereotype(Annotated annotated, Class<? extends Annotation> annotationType, BeanManager beanManager) {
        return isAnnotationPresentOnStereotype(annotated.getAnnotations(), annotationType, beanManager);
    }

    /**
     * Inspect the {@link AnnotatedElement} and retrieve the specified annotation
     * type, if present. This method discovers annotations defined on
     * the element as well as annotations inherited from a CDI &#064;
     * {@link Stereotype} on the element.
     *
     * @param element        The element to inspect
     * @param annotationType The annotation type to expect
     * @param beanManager    The CDI BeanManager instance
     * @return The annotation instance found on this element or null if no
     *         matching annotation was found.
     */
    public static <A extends Annotation> A getAnnotation(AnnotatedElement element, Class<A> annotationType, BeanManager beanManager) {
        if (element.isAnnotationPresent(annotationType)) {
            return annotationType.cast(element.getAnnotation(annotationType));
        }

        return getAnnotationFromStereotype(Arrays.asList(element.getAnnotations()), annotationType, beanManager);
    }

    /**
     * Inspect the {@link Annotated} and retrieve the specified annotation
     * type, if present. This method discovers annotations defined on
     * the element as well as annotations inherited from a CDI &#064;
     * {@link Stereotype} on the element.
     *
     * @param annotated      The element to inspect
     * @param annotationType The annotation type to expect
     * @param beanManager    The CDI BeanManager instance
     * @return The annotation instance found on this element or null if no
     *         matching annotation was found.
     */
    public static <A extends Annotation> A getAnnotation(Annotated annotated, Class<A> annotationType, BeanManager beanManager) {
        if (annotated.isAnnotationPresent(annotationType)) {
            return annotationType.cast(annotated.getAnnotation(annotationType));
        }

        return getAnnotationFromStereotype(annotated.getAnnotations(), annotationType, beanManager);
    }

    /**
     * Inspect the {@link AnnotatedElement} for a specific annotation type. If the
     * transitive argument is <code>true</code> , this method also discovers
     * annotations inherited from a CDI &#064; {@link Stereotype} on the element.
     *
     * @param element        The element to inspect
     * @param annotationType The annotation type to expect
     * @param transitive     Whether the annotation may be used as a meta-annotation
     *                       or not
     * @param beanManager    The CDI BeanManager instance
     * @return The annotation instance found on this element or null if no
     *         matching annotation was found.
     */
    public static <A extends Annotation> A getAnnotation(AnnotatedElement element, final Class<A> annotationType, boolean transitive, BeanManager beanManager) {
        if (transitive) {
            return getAnnotation(element, annotationType, beanManager);
        } else {
            return element.getAnnotation(annotationType);
        }
    }

    /**
     * Discover if the {@link AnnotatedElement} has been annotated with a &#064;
     * {@link Stereotype} that provides the annotation type and return it.
     *
     * @param element        The element to inspect
     * @param annotationType The annotation type to expect
     * @param beanManager    The CDI BeanManager instance
     * @return The annotation instance found on this element or null if no
     *         matching annotation was found.
     */
    public static <A extends Annotation> A getAnnotationFromStereotype(AnnotatedElement element, Class<A> annotationType, BeanManager beanManager) {
        return getAnnotationFromStereotype(Arrays.asList(element.getAnnotations()), annotationType, beanManager);
    }

    /**
     * Discover if the {@link Annotated} has been annotated with a &#064;
     * {@link Stereotype} that provides the specified annotation type and
     * return it.
     *
     * @param element        The element to inspect.
     * @param annotationType The annotation type to expect
     * @param beanManager    The CDI BeanManager instance
     * @return The annotation instance found on this element or null if no
     *         matching annotation was found.
     */
    public static <A extends Annotation> A getAnnotationFromStereotype(Annotated annotated, Class<A> annotationType, BeanManager beanManager) {
        return getAnnotationFromStereotype(annotated.getAnnotations(), annotationType, beanManager);
    }

    /**
     * Inspects an annotated element for the given meta annotation. This should
     * only be used for user defined meta annotations, where the annotation must
     * be physically present.
     *
     * @param element        The element to inspect
     * @param annotationType The meta annotation to search for
     * @return The annotation instance found on this element or null if no
     *         matching annotation was found.
     */
    public static <A extends Annotation> A getMetaAnnotation(Annotated element, final Class<A> annotationType) {
        for (Annotation annotation : element.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(annotationType)) {
                return annotation.annotationType().getAnnotation(annotationType);
            }
        }
        return null;
    }

    /**
     * Inspects an annotated element for any annotations with the given meta
     * annotation. This should only be used for user defined meta annotations,
     * where the annotation must be physically present.
     *
     * @param element        The element to inspect
     * @param annotationType The meta annotation to search for
     * @return The annotation instances found on this element or an empty set if
     *         no matching meta-annotation was found.
     */
    public static Set<Annotation> getAnnotations(Annotated element, final Class<? extends Annotation> metaAnnotationType) {
        Set<Annotation> annotations = new HashSet<Annotation>();
        for (Annotation annotation : element.getAnnotations()) {
            if (annotation.annotationType().isAnnotationPresent(metaAnnotationType)) {
                annotations.add(annotation);
            }
        }
        return annotations;
    }

    private static boolean isAnnotationPresentOnStereotype(Collection<Annotation> annotations, Class<? extends Annotation> annotationType, BeanManager beanManager) {
        for (Annotation candidate : annotations) {
            if (beanManager.isStereotype(candidate.annotationType())) {
                for (Annotation stereotyped : beanManager.getStereotypeDefinition(candidate.annotationType())) {
                    if (stereotyped.annotationType().equals(annotationType)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private static <A extends Annotation> A getAnnotationFromStereotype(Collection<Annotation> annotations, Class<A> annotationType, BeanManager beanManager) {
        for (Annotation candidate : annotations) {
            if (beanManager.isStereotype(candidate.annotationType())) {
                for (Annotation stereotyped : beanManager.getStereotypeDefinition(candidate.annotationType())) {
                    if (stereotyped.annotationType().equals(annotationType)) {
                        return annotationType.cast(stereotyped);
                    }
                }
            }
        }

        return null;
    }
}
