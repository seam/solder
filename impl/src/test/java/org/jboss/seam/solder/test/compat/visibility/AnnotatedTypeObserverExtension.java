package org.jboss.seam.solder.test.compat.visibility;

import java.util.ArrayList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

public class AnnotatedTypeObserverExtension<X> implements Extension {

    public List<Class<?>> observed;

    public AnnotatedTypeObserverExtension() {
        observed = new ArrayList<Class<?>>();
    }

    public void processAnnotatedType(@Observes ProcessAnnotatedType<X> event) {
        observed.add(event.getAnnotatedType().getJavaClass());
    }
}
