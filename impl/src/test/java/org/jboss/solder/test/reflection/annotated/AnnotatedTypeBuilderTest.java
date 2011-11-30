package org.jboss.solder.test.reflection.annotated;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.inject.Named;

import org.jboss.solder.reflection.annotated.AnnotatedTypeBuilder;
import org.junit.Test;

/**
 * 
 * @author <a href="http://community.jboss.org/people/jharting">Jozef Hartinger</a>
 *
 */
public class AnnotatedTypeBuilderTest {

    /**
     * @see <a href="https://issues.jboss.org/browse/SOLDER-103">SOLDER-103</a>
     */
    @Test
    public void testTypeLevelAnnotationRedefinition()
    {
        AnnotatedTypeBuilder<Cat> builder = new AnnotatedTypeBuilder<Cat>();
        builder.readFromType(Cat.class);
        builder.redefine(Named.class, new NamedAnnotationRedefiner());
        
        AnnotatedType<Cat> cat = builder.create();
        assertEquals(3, cat.getAnnotations().size());
        assertTrue(cat.isAnnotationPresent(Named.class));
        assertTrue(cat.isAnnotationPresent(Alternative.class));
        assertTrue(cat.isAnnotationPresent(ApplicationScoped.class));
        assertEquals("tomcat", cat.getAnnotation(Named.class).value());
    }
}
