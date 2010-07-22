package org.jboss.weld.extensions.annotated;

import java.lang.annotation.Annotation;
import java.lang.reflect.Member;

public interface MemberAnnotationRedefiner<T extends Annotation> extends AnnotationRedefiner<T, Member>
{

}
