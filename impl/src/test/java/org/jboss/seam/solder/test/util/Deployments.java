package org.jboss.seam.solder.test.util;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class Deployments
{
   public static WebArchive baseDeployment()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war").addLibrary(MavenArtifactResolver.resolve("org.jboss.seam.solder", "seam-solder")).addWebResource(new ByteArrayAsset(new byte[0]), "beans.xml");
   }
}
