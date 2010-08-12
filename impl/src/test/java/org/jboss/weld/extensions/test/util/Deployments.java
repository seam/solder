package org.jboss.weld.extensions.test.util;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.ByteArrayAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public class Deployments
{

   public static WebArchive baseDeployment()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war").addLibrary(MavenArtifactResolver.resolve("org.jboss.weld", "weld-extensions")).addWebResource(new ByteArrayAsset(new byte[0]), "beans.xml");
   }
   
}
