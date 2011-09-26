package org.jboss.seam.config.examples.quiz;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface QuizQualifier {

    String value();
}
