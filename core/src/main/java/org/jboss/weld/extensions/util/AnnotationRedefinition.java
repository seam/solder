package org.jboss.weld.extensions.util;

import java.lang.annotation.Annotation;

public interface AnnotationRedefinition<X extends Annotation>
{
   public X redefine(X annotation, Reannotated reannotated);
}
