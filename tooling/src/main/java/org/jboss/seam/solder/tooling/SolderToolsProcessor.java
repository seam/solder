/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat, Inc., and individual contributors
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
package org.jboss.seam.solder.tooling;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Types;

import org.jboss.logging.AbstractTool;
import org.jboss.logging.Annotations;
import org.jboss.logging.Loggers;
import org.jboss.logging.LoggingTools;
import org.jboss.logging.ToolLogger;
import org.jboss.logging.generator.ImplementorClassGenerator;
import org.jboss.logging.generator.TranslationClassGenerator;
import org.jboss.logging.generator.TranslationFileGenerator;
import org.jboss.logging.validation.ValidationErrorMessage;
import org.jboss.logging.validation.Validator;

import static javax.lang.model.util.ElementFilter.typesIn;
import static org.jboss.logging.util.ElementHelper.getInterfaceMethods;

/**
 * The main annotation processor for Solder.
 *
 * @author <a href="http://community.jboss.org/people/kenfinni">Ken Finnigan</a>
 */
@SupportedAnnotationTypes("*")
@SupportedOptions({
        SolderToolsProcessor.DEBUG_OPTION
})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class SolderToolsProcessor extends AbstractProcessor {
    public static final String DEBUG_OPTION = "debug";
    private static final boolean ALLOW_OTHER_ANNOTATION_PROCESSOR_TO_PROCESS = true;
    private final List<AbstractTool> processors;
    private Annotations annotations;
    private Loggers loggers;
    private ToolLogger logger;

    /**
     * Default constructor.
     */
    public SolderToolsProcessor() {
        this.processors = new ArrayList<AbstractTool>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(final ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        logger = ToolLogger.getLogger(processingEnv);
        annotations = LoggingTools.findAnnotations();
        loggers = LoggingTools.findLoggers();

        //Tools generator -  Note the order these are executed in.
        processors.add(new ImplementorClassGenerator(processingEnv, annotations, loggers));
        processors.add(new TranslationClassGenerator(processingEnv, annotations, loggers));
        processors.add(new TranslationFileGenerator(processingEnv, annotations, loggers));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<String> getSupportedOptions() {
        Set<String> supportedOptions = new HashSet<String>();

        //Add global options
        SupportedOptions globalOptions = this.getClass().getAnnotation(SupportedOptions.class);
        if (globalOptions != null) {
            supportedOptions.addAll(Arrays.asList(globalOptions.value()));
        }

        //Add tool processors options
        for (AbstractTool generator : processors) {
            supportedOptions.addAll(generator.getSupportedOptions());
        }

        return supportedOptions;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean process(final Set<? extends TypeElement> annotations, final RoundEnvironment roundEnv) {

        Types typesUtil = processingEnv.getTypeUtils();
        Validator validator = Validator.buildValidator(processingEnv, this.annotations);

        //Call solder logging tools
        for (TypeElement annotation : annotations) {
            if (isValidAnnotation(annotation)) {
                Set<? extends TypeElement> elements = typesIn(roundEnv.getElementsAnnotatedWith(annotation));
                Collection<ValidationErrorMessage> errorMessages = validator.validate(elements);

                if (!errorMessages.isEmpty()) {

                    for (ValidationErrorMessage error : errorMessages) {
                        logger.error(error.getElement(), error.getMessage());
                    }

                } else {

                    for (TypeElement element : elements) {

                        if (element.getKind().isInterface()
                                && !element.getModifiers().contains(Modifier.PRIVATE)) {

                            Collection<ExecutableElement> methods = getInterfaceMethods(element, typesUtil, loggers);

                            for (AbstractTool processor : processors) {
                                logger.debug("Executing processor %s", processor.getName());
                                processor.processTypeElement(annotation, element, methods);
                            }
                        }
                    }

                }
            }

        }

        return ALLOW_OTHER_ANNOTATION_PROCESSOR_TO_PROCESS;
    }

    private boolean isValidAnnotation(final TypeElement annotation) {
        final String name = annotation.getQualifiedName().toString();
        return (name.equals(annotations.messageBundle().getName()) || name.equals(annotations.messageLogger().getName()));
    }
}
