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
package org.jboss.solder.core;

import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.AfterBeanDiscovery;
import javax.enterprise.inject.spi.AnnotatedConstructor;
import javax.enterprise.inject.spi.AnnotatedField;
import javax.enterprise.inject.spi.AnnotatedMethod;
import javax.enterprise.inject.spi.AnnotatedParameter;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.BeforeBeanDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.inject.Named;

import org.jboss.solder.logging.Logger;
import org.jboss.solder.core.Exact;
import org.jboss.solder.core.FullyQualified;
import org.jboss.solder.core.Requires;
import org.jboss.solder.core.VersionLoggerUtil;
import org.jboss.solder.core.Veto;
import org.jboss.solder.literal.NamedLiteral;
import org.jboss.solder.properties.Properties;
import org.jboss.solder.reflection.Reflections;
import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;

/**
 * Extension to install the "core" extensions. Core extensions are those that
 * add additional abilities to CDI applications via annotations.
 *
 * @author Stuart Douglas
 * @author Pete Muir
 * @author Dan Allen
 * @author Gavin King
 * @author Jozef Hartinger
 */
public class CoreExtension implements Extension {
    private final Collection<Bean<?>> additionalBeans;

    static final Logger log = Logger.getLogger(CoreExtension.class);

    //this must be public for the service loader to work properly
    public CoreExtension() {
        this.additionalBeans = new ArrayList<Bean<?>>();
    }

    void beforeBeanDiscovery(@Observes final BeforeBeanDiscovery bbd) {
        VersionLoggerUtil.logVersionInformation(this.getClass(),
                "Solder %s (build id: %s)",
                "org.jboss.solder.Version");
    }

    <X> void processAnnotatedType(@Observes final ProcessAnnotatedType<X> pat, BeanManager beanManager) {
        final AnnotatedType<X> annotatedType = pat.getAnnotatedType();
        final Class<X> javaClass = annotatedType.getJavaClass();
        final Package pkg = javaClass.getPackage();

        // Support for @Veto
        if (annotatedType.isAnnotationPresent(Veto.class) || (pkg != null && pkg.isAnnotationPresent(Veto.class))) {
            pat.veto();
            log.info("Preventing " + javaClass + " from being installed as bean due to @Veto annotation");
            return;
        }

        // Support for @Requires
        Set<String> requiredClasses = new HashSet<String>();
        // package-level @Requires
        if (pkg != null && pkg.isAnnotationPresent(Requires.class)) {
            String[] packageRequiredClasses = pkg.getAnnotation(Requires.class).value();
            requiredClasses.addAll(Arrays.asList(packageRequiredClasses));
        }
        // class-level @Requires
        if (annotatedType.isAnnotationPresent(Requires.class)) {
            String[] typeRequiredClasses = annotatedType.getAnnotation(Requires.class).value();
            requiredClasses.addAll(Arrays.asList(typeRequiredClasses));
        }
        if (!requiredClasses.isEmpty()) {
            for (String i : requiredClasses) {
                try {
                    Reflections.classForName(i, javaClass.getClassLoader());
                } catch (ClassNotFoundException e) {
                    log.info("Preventing " + javaClass + " from being installed as required class " + i + " could not be found");
                    pat.veto();
                } catch (LinkageError e) {
                    // LinkageError is a superclass of NoClassDefFoundError
                    log.info("Preventing " + javaClass + " from being installed as a linkage error occurred loading required class " + i + ". The linkage error was " + e.toString());
                    pat.veto();
                }
            }
        }

        AnnotatedTypeBuilder<X> builder = null;

        // support for @Named packages
        Named namedFromPackage = null;
        if (pkg != null && pkg.isAnnotationPresent(Named.class) && !annotatedType.isAnnotationPresent(Named.class)) {
            builder = initializeBuilder(builder, annotatedType);
            namedFromPackage = new NamedLiteral();
            builder.addToClass(namedFromPackage);
        }

        FullyQualified qualifiedOnPackage = null;
        if (pkg != null) {
            qualifiedOnPackage = pkg.getAnnotation(FullyQualified.class);
        }

        // support for @FullyQualified bean names on type (respect @Named if added by previous operation)
        if ((namedFromPackage != null || annotatedType.isAnnotationPresent(Named.class)) &&
                (qualifiedOnPackage != null || annotatedType.isAnnotationPresent(FullyQualified.class))) {
            builder = initializeBuilder(builder, annotatedType);
            String name = (namedFromPackage != null ? namedFromPackage.value() : annotatedType.getAnnotation(Named.class).value());
            if (name.length() == 0) {
                name = deriveBeanNameForType(javaClass);
            }
            Package targetPackage = resolveTargetPackage(annotatedType.getAnnotation(FullyQualified.class), qualifiedOnPackage, pkg);
            builder.removeFromClass(Named.class); // add w/o remove was failing in cases
            builder.addToClass(new NamedLiteral(qualify(targetPackage, name)));
        }

        // support for @Exact fields
        // support for @FullyQualified @Named producer fields
        for (AnnotatedField<? super X> f : annotatedType.getFields()) {
            if (f.isAnnotationPresent(Exact.class)) {
                Class<?> type = f.getAnnotation(Exact.class).value();
                builder = initializeBuilder(builder, annotatedType);
                builder.overrideFieldType(f, type);
            }

            if (f.isAnnotationPresent(Produces.class) && f.isAnnotationPresent(Named.class) &&
                    (qualifiedOnPackage != null || f.isAnnotationPresent(FullyQualified.class))) {
                String name = f.getAnnotation(Named.class).value();
                if (name.length() == 0) {
                    name = f.getJavaMember().getName();
                }
                Package targetPackage = resolveTargetPackage(f.getAnnotation(FullyQualified.class), qualifiedOnPackage, pkg);
                builder.removeFromField(f, Named.class); // add w/o remove was failing in cases
                builder.addToField(f, new NamedLiteral(qualify(targetPackage, name)));
            }
        }
        // support for @Exact method parameters
        // support for @FullyQualified @Named producer methods
        for (AnnotatedMethod<? super X> m : annotatedType.getMethods()) {
            for (AnnotatedParameter<? super X> p : m.getParameters()) {
                if (p.isAnnotationPresent(Exact.class)) {
                    Class<?> type = p.getAnnotation(Exact.class).value();
                    builder = initializeBuilder(builder, annotatedType);
                    builder.overrideParameterType(p, type);
                }
            }

            if (m.isAnnotationPresent(Produces.class) && m.isAnnotationPresent(Named.class) &&
                    (qualifiedOnPackage != null || m.isAnnotationPresent(FullyQualified.class))) {
                String name = m.getAnnotation(Named.class).value();
                if (name.length() == 0) {
                    if (Properties.isProperty(m.getJavaMember())) {
                        name = Properties.createProperty(m.getJavaMember()).getName();
                    } else {
                        name = m.getJavaMember().getName();
                    }
                }
                Package targetPackage = resolveTargetPackage(m.getAnnotation(FullyQualified.class), qualifiedOnPackage, pkg);
                builder.removeFromMethod(m, Named.class); // add w/o remove was failing in cases
                builder.addToMethod(m, new NamedLiteral(qualify(targetPackage, name)));
            }
        }
        // support for @Exact constructor parameters
        for (AnnotatedConstructor<X> c : annotatedType.getConstructors()) {
            for (AnnotatedParameter<? super X> p : c.getParameters()) {
                if (p.isAnnotationPresent(Exact.class)) {
                    Class<?> type = p.getAnnotation(Exact.class).value();
                    builder = initializeBuilder(builder, annotatedType);
                    builder.overrideParameterType(p, type);
                }
            }
        }
        if (builder != null) {
            pat.setAnnotatedType(builder.create());
        }
    }

    void afterBeanDiscovery(@Observes AfterBeanDiscovery abd, BeanManager beanManager) {
        failIfWeldExtensionsDetected(beanManager);
        for (Bean<?> bean : additionalBeans) {
            abd.addBean(bean);
        }
    }

    private void failIfWeldExtensionsDetected(BeanManager beanManager) {
        for (Iterator<Bean<?>> extensions = beanManager.getBeans(Extension.class).iterator(); extensions.hasNext();) {
            if (extensions.next().getBeanClass().getName().equals("org.jboss.weld.extensions.core.CoreExtension")) {
                throw new IllegalStateException("Both Weld Extensions and Solder libraries detected on the classpath. " +
                        "If you're migrating to Solder, please remove Weld Extensions from the deployment.");
            }
        }
    }

    private <X> AnnotatedTypeBuilder<X> initializeBuilder(final AnnotatedTypeBuilder<X> currentBuilder, final AnnotatedType<X> source) {
        if (currentBuilder == null) {
            return new AnnotatedTypeBuilder<X>().readFromType(source);
        }
        return currentBuilder;
    }

    private String qualify(final Package pkg, final String name) {
        return pkg.getName() + "." + name;
    }

    private Package resolveTargetPackage(final FullyQualified qualifiedOnElement, final FullyQualified qualifiedOnPackage, final Package currentPackage) {
        FullyQualified qualified = qualifiedOnElement != null ? qualifiedOnElement : qualifiedOnPackage;
        if (qualified.value() == Class.class) {
            return currentPackage;
        } else {
            return qualified.value().getPackage();
        }
    }

    private String deriveBeanNameForType(Class<?> type) {
        return Introspector.decapitalize(type.getSimpleName());
    }
}
