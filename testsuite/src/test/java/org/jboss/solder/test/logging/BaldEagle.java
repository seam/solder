package org.jboss.solder.test.logging;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.jboss.logging.Category;

@SessionScoped
public class BaldEagle implements Serializable {
    @Inject
    @Category("Birds")
    private BirdLogger logger;

    public void generateLogMessage() {
        logger.logBaldEaglesSpotted(2);
    }
}
