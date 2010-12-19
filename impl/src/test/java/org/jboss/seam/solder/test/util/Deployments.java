package org.jboss.seam.solder.test.util;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class Deployments
{
   public static WebArchive baseDeployment()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
         .addLibraries(
               MavenArtifactResolver.resolve("org.jboss.logging", "jboss-logging"),
               MavenArtifactResolver.resolve("org.jboss.seam.solder", "seam-solder-api"),
               MavenArtifactResolver.resolve("org.jboss.seam.solder", "seam-solder-impl"))
         .addWebResource(EmptyAsset.INSTANCE, "beans.xml");
   }
}
