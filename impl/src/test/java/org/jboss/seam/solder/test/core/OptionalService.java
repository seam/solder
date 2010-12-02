package org.jboss.seam.solder.test.core;

import org.jboss.seam.solder.core.Requires;

@Requires( { "class.does.not.exist.SomeClass", "org.jboss.seam.solder.test.core.Greyhound", "org.jboss.seam.solder.test.core.CoreTest" })
public class OptionalService
{

}
