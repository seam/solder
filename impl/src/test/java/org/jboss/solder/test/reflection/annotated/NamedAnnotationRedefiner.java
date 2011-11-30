package org.jboss.solder.test.reflection.annotated;

import javax.inject.Named;

import org.jboss.solder.literal.AlternativeLiteral;
import org.jboss.solder.literal.ApplicationScopedLiteral;
import org.jboss.solder.literal.NamedLiteral;
import org.jboss.solder.reflection.annotated.AnnotationBuilder;
import org.jboss.solder.reflection.annotated.AnnotationRedefiner;
import org.jboss.solder.reflection.annotated.RedefinitionContext;

class NamedAnnotationRedefiner implements AnnotationRedefiner<Named> {

    @Override
    public void redefine(RedefinitionContext<Named> ctx) {
        Named named = ctx.getAnnotatedElement().getAnnotation(Named.class);
        if ("cat".equals(named.value()))
        {
            AnnotationBuilder builder = ctx.getAnnotationBuilder();
            // add two annotations
            builder.add(new AlternativeLiteral());
            builder.add(new ApplicationScopedLiteral());
            // change the value of @Named
            builder.remove(Named.class);
            builder.add(new NamedLiteral("tomcat"));
        }
    }

}
