package org.jboss.weld.extensions.test.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class MavenArtifactPathResolverTest
{
   
   String groupId = "org.jboss.weld";
   String artifactId = "weld-extensions";

   @Test
   public void testWindows()
   {      
      char fileSeparator = '\\';
      char pathSeparator = ';';      
      
      String classPath = "V:\\workspace\\extensions\\impl\\target\\test-classes;V:\\workspace\\extensions\\impl\\target\\classes;C:\\Users\\john.doe\\.m2\\repository\\org\\javassist\\javassist\\3.13.0-GA\\javassist-3.13.0-GA.jar;C:\\Users\\john.doe\\.m2\\repository\\javax\\enterprise\\cdi-api\\1.0-SP2\\cdi-api-1.0-SP2.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\spec\\javax\\interceptor\\jboss-interceptors-api_1.1_spec\\1.0.0.Beta1\\jboss-interceptors-api_1.1_spec-1.0.0.Beta1.jar;C:\\Users\\john.doe\\.m2\\repository\\javax\\annotation\\jsr250-api\\1.0\\jsr250-api-1.0.jar;C:\\Users\\john.doe\\.m2\\repository\\javax\\inject\\javax.inject\\1\\javax.inject-1.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\slf4j\\slf4j-api\\1.5.10\\slf4j-api-1.5.10.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\weld\\weld-core\\1.1.0-SNAPSHOT\\weld-core-1.1.0-SNAPSHOT.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\weld\\weld-api\\1.1-SNAPSHOT\\weld-api-1.1-SNAPSHOT.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\weld\\weld-spi\\1.1-SNAPSHOT\\weld-spi-1.1-SNAPSHOT.jar;C:\\Users\\john.doe\\.m2\\repository\\com\\google\\guava\\guava\\r06\\guava-r06.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\interceptor\\jboss-interceptor\\1.0.0-CR11\\jboss-interceptor-1.0.0-CR11.jar;C:\\Users\\john.doe\\.m2\\repository\\javassist\\javassist\\3.11.0.GA\\javassist-3.11.0.GA.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\slf4j\\slf4j-ext\\1.5.10\\slf4j-ext-1.5.10.jar;C:\\Users\\john.doe\\.m2\\repository\\ch\\qos\\cal10n\\cal10n-api\\0.7.2\\cal10n-api-0.7.2.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\ejb3\\jboss-ejb3-api\\3.1.0\\jboss-ejb3-api-3.1.0.jar;C:\\Users\\john.doe\\.m2\\repository\\javax\\servlet\\servlet-api\\2.5\\servlet-api-2.5.jar;C:\\Users\\john.doe\\.m2\\repository\\junit\\junit\\4.8.1\\junit-4.8.1.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\arquillian\\arquillian-junit\\1.0.0.Alpha3\\arquillian-junit-1.0.0.Alpha3.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\arquillian\\arquillian-impl-base\\1.0.0.Alpha3\\arquillian-impl-base-1.0.0.Alpha3.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\arquillian\\arquillian-api\\1.0.0.Alpha3\\arquillian-api-1.0.0.Alpha3.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\shrinkwrap\\shrinkwrap-api\\1.0.0-alpha-11\\shrinkwrap-api-1.0.0-alpha-11.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\arquillian\\arquillian-spi\\1.0.0.Alpha3\\arquillian-spi-1.0.0.Alpha3.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\shrinkwrap\\shrinkwrap-impl-base\\1.0.0-alpha-11\\shrinkwrap-impl-base-1.0.0-alpha-11.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\shrinkwrap\\shrinkwrap-spi\\1.0.0-alpha-11\\shrinkwrap-spi-1.0.0-alpha-11.jar;C:\\Users\\john.doe\\.m2\\repository\\javax\\el\\el-api\\2.2\\el-api-2.2.jar;C:\\Users\\john.doe\\.m2\\repository\\el-impl\\el-impl\\1.0\\el-impl-1.0.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\arquillian\\container\\arquillian-weld-ee-embedded-1.1\\1.0.0.Alpha3\\arquillian-weld-ee-embedded-1.1-1.0.0.Alpha3.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\arquillian\\protocol\\arquillian-protocol-local\\1.0.0.Alpha3\\arquillian-protocol-local-1.0.0.Alpha3.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\arquillian\\testenricher\\arquillian-testenricher-cdi\\1.0.0.Alpha3\\arquillian-testenricher-cdi-1.0.0.Alpha3.jar;C:\\Users\\john.doe\\.m2\\repository\\org\\jboss\\shrinkwrap\\shrinkwrap-extension-classloader\\1.0.0-alpha-11\\shrinkwrap-extension-classloader-1.0.0-alpha-11.jar;C:\\Users\\john.doe\\.m2\\repository\\javax\\transaction\\jta\\1.1\\jta-1.1.jar;C:\\Users\\john.doe\\.m2\\repository\\javax\\persistence\\persistence-api\\1.0\\persistence-api-1.0.jar;C:\\Users\\john.doe\\.m2\\repository\\javax\\validation\\validation-api\\1.0.0.GA\\validation-api-1.0.0.GA.jar;/V:/eclipse/configuration/org.eclipse.osgi/bundles/311/1/.cp/;/V:/eclipse/configuration/org.eclipse.osgi/bundles/309/1/.cp/;/V:/eclipse/configuration/org.eclipse.osgi/bundles/310/1/.cp/";
      
      Matcher matches = MavenArtifactResolver.getRepoMatcher(groupId, artifactId, classPath, pathSeparator, fileSeparator);

      if(matches.find())
      {
         //TODO What does this do?
      }
      
      String localClasses = MavenArtifactResolver.getLocalClassesString(fileSeparator);      
      Matcher localClassesMatcher = MavenArtifactResolver.getLocalClassesMatcher(classPath, localClasses, pathSeparator);
      
      if(!localClassesMatcher.find())
      {
         throw new IllegalArgumentException("Unable to find maven archive " + groupId + ":" + artifactId + " in the local build");
      }
      
      String path = localClassesMatcher.group();
      String trimmedPath = path.substring(0, path.length() - 8);
      
      assertTrue("Path was: " + path, path.equals("V:\\workspace\\extensions\\impl\\target\\classes"));
      assertTrue("Trimmed was: " + trimmedPath, trimmedPath.equals("V:\\workspace\\extensions\\impl\\target"));

   }
   
   @Test
   public void testLinux()
   {    
      char fileSeparator = '/';
      char pathSeparator = ':';
      
      String classPath = "/home/user/workspace/extensions/impl/target/test-classes:/home/jdoe/workspace/extensions/impl/target/classes:/home/jdoe/.m2/repository/org/javassist/javassist/3.13.0-GA/javassist-3.13.0-GA.jar:/home/jdoe/.m2/repository/javax/enterprise/cdi-api/1.0-SP2/cdi-api-1.0-SP2.jar:/home/jdoe/.m2/repository/org/jboss/spec/javax/interceptor/jboss-interceptors-api_1.1_spec/1.0.0.Beta1/jboss-interceptors-api_1.1_spec-1.0.0.Beta1.jar:/home/jdoe/.m2/repository/javax/annotation/jsr250-api/1.0/jsr250-api-1.0.jar:/home/jdoe/.m2/repository/javax/inject/javax.inject/1/javax.inject-1.jar:/home/jdoe/.m2/repository/org/slf4j/slf4j-api/1.5.10/slf4j-api-1.5.10.jar:/home/jdoe/.m2/repository/org/jboss/weld/weld-core/1.1.0-SNAPSHOT/weld-core-1.1.0-SNAPSHOT.jar:/home/jdoe/.m2/repository/org/jboss/weld/weld-api/1.1-SNAPSHOT/weld-api-1.1-SNAPSHOT.jar:/home/jdoe/.m2/repository/org/jboss/weld/weld-spi/1.1-SNAPSHOT/weld-spi-1.1-SNAPSHOT.jar:/home/jdoe/.m2/repository/com/google/guava/guava/r06/guava-r06.jar:/home/jdoe/.m2/repository/org/jboss/interceptor/jboss-interceptor/1.0.0-CR11/jboss-interceptor-1.0.0-CR11.jar:/home/jdoe/.m2/repository/javassist/javassist/3.11.0.GA/javassist-3.11.0.GA.jar:/home/jdoe/.m2/repository/org/slf4j/slf4j-ext/1.5.10/slf4j-ext-1.5.10.jar:/home/jdoe/.m2/repository/ch/qos/cal10n/cal10n-api/0.7.2/cal10n-api-0.7.2.jar:/home/jdoe/.m2/repository/org/jboss/ejb3/jboss-ejb3-api/3.1.0/jboss-ejb3-api-3.1.0.jar:/home/jdoe/.m2/repository/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar:/home/jdoe/.m2/repository/junit/junit/4.8.1/junit-4.8.1.jar:/home/jdoe/.m2/repository/org/jboss/arquillian/arquillian-junit/1.0.0.Alpha3/arquillian-junit-1.0.0.Alpha3.jar:/home/jdoe/.m2/repository/org/jboss/arquillian/arquillian-impl-base/1.0.0.Alpha3/arquillian-impl-base-1.0.0.Alpha3.jar:/home/jdoe/.m2/repository/org/jboss/arquillian/arquillian-api/1.0.0.Alpha3/arquillian-api-1.0.0.Alpha3.jar:/home/jdoe/.m2/repository/org/jboss/shrinkwrap/shrinkwrap-api/1.0.0-alpha-11/shrinkwrap-api-1.0.0-alpha-11.jar:/home/jdoe/.m2/repository/org/jboss/arquillian/arquillian-spi/1.0.0.Alpha3/arquillian-spi-1.0.0.Alpha3.jar:/home/jdoe/.m2/repository/org/jboss/shrinkwrap/shrinkwrap-impl-base/1.0.0-alpha-11/shrinkwrap-impl-base-1.0.0-alpha-11.jar:/home/jdoe/.m2/repository/org/jboss/shrinkwrap/shrinkwrap-spi/1.0.0-alpha-11/shrinkwrap-spi-1.0.0-alpha-11.jar:/home/jdoe/.m2/repository/javax/el/el-api/2.2/el-api-2.2.jar:/home/jdoe/.m2/repository/el-impl/el-impl/1.0/el-impl-1.0.jar:/home/jdoe/.m2/repository/org/jboss/arquillian/container/arquillian-weld-ee-embedded-1.1/1.0.0.Alpha3/arquillian-weld-ee-embedded-1.1-1.0.0.Alpha3.jar:/home/jdoe/.m2/repository/org/jboss/arquillian/protocol/arquillian-protocol-local/1.0.0.Alpha3/arquillian-protocol-local-1.0.0.Alpha3.jar:/home/jdoe/.m2/repository/org/jboss/arquillian/testenricher/arquillian-testenricher-cdi/1.0.0.Alpha3/arquillian-testenricher-cdi-1.0.0.Alpha3.jar:/home/jdoe/.m2/repository/org/jboss/shrinkwrap/shrinkwrap-extension-classloader/1.0.0-alpha-11/shrinkwrap-extension-classloader-1.0.0-alpha-11.jar:/home/jdoe/.m2/repository/javax/transaction/jta/1.1/jta-1.1.jar:/home/jdoe/.m2/repository/javax/persistence/persistence-api/1.0/persistence-api-1.0.jar:/home/jdoe/.m2/repository/javax/validation/validation-api/1.0.0.GA/validation-api-1.0.0.GA.jar:/home/jdoe/.eclipse/org.eclipse.platform_3.5.0_155965261/configuration/org.eclipse.osgi/bundles/112/1/.cp/:/home/jdoe/.eclipse/org.eclipse.platform_3.5.0_155965261/configuration/org.eclipse.osgi/bundles/110/1/.cp/:/home/jdoe/.eclipse/org.eclipse.platform_3.5.0_155965261/configuration/org.eclipse.osgi/bundles/111/1/.cp/";
      
      Matcher matches = MavenArtifactResolver.getRepoMatcher(groupId, artifactId, classPath, pathSeparator, fileSeparator);
      
      if(matches.find())
      {
         //TODO What does this do?
      }
      
      String localClasses = MavenArtifactResolver.getLocalClassesString(fileSeparator);      
      Matcher localClassesMatcher = MavenArtifactResolver.getLocalClassesMatcher(classPath, localClasses, pathSeparator);
      
      if(!localClassesMatcher.find())
      {
         throw new IllegalArgumentException("Unable to find maven archive " + groupId + ":" + artifactId + " in the local build");
      }
      
      String path = localClassesMatcher.group();
      String trimmedPath = path.substring(0, path.length() - 8);
      
      assertTrue("Path was: " + path, path.equals("/home/jdoe/workspace/extensions/impl/target/classes"));
      assertTrue("Trimmed was: " + trimmedPath, trimmedPath.equals("/home/jdoe/workspace/extensions/impl/target"));

   }
   
   
   public Pattern getRepoPattern(char pathSeparator, char fileSeparator)
   {
      return Pattern.compile("^[" + pathSeparator +"]*" + Pattern.quote(getPathString(fileSeparator)) + "^*[" + pathSeparator + "]*", Pattern.CASE_INSENSITIVE);
   }
   
   public Pattern getLocalClassesPattern(String localClasses, char pathSeparator)
   {
      return Pattern.compile("[^" + pathSeparator + "]*" + localClasses + "[^" + pathSeparator + "]*", Pattern.CASE_INSENSITIVE);
   }
   
   public String getPathString(char fileSeparator)
   {
      return groupId.replace('.', fileSeparator) + fileSeparator + artifactId;
   }
   
}
