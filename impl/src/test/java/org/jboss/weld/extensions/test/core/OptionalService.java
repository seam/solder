package org.jboss.weld.extensions.test.core;

import org.jboss.weld.extensions.core.RequireClasses;

@RequireClasses( { "class.does.not.exist.SomeClass", "org.jboss.weld.extensions.test.core.Greyhound", "org.jboss.weld.extensions.test.core.CoreTest" })
public class OptionalService
{

}
