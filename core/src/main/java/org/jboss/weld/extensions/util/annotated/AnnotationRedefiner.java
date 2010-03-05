package org.jboss.weld.extensions.util.annotated;

import java.lang.annotation.Annotation;

public interface AnnotationRedefiner<X extends Annotation>
{
   public X redefine(X annotation, AnnotationBuilder annotations);
}
