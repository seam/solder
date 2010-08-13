package org.jboss.weld.extensions.test.util;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves a maven artifact present on the test classpath.
 * 
 * @author Stuart Douglas
 * 
 */
public class MavenArtifactResolver
{

   public static File resolve(String groupId, String artifactId)
   {
      String classPath = System.getProperty("java.class.path");
      // first look for an artifact from the repo     
      Matcher matches = getRepoMatcher(groupId, artifactId, classPath, File.pathSeparatorChar, File.separatorChar);

      if (!matches.find())
      {
         // find a resource from the local build
         String localClasses = getLocalClassesString(File.separatorChar);
         Matcher localClassesMatcher = getLocalClassesMatcher(classPath, localClasses, File.pathSeparatorChar);
         if (!localClassesMatcher.find())
         {
            throw new IllegalArgumentException("Unable to find maven archive " + groupId + ":" + artifactId + " in the local build");
         }
         else
         {
            List<String> targetPaths = new ArrayList<String>();
            do
            {
               String path = localClassesMatcher.group();
               targetPaths.add(path.substring(0, path.length() - 8));
            }
            while (localClassesMatcher.find());
            String fileName = findBuiltArtifact(targetPaths, artifactId);
            if (fileName == null)
            {
               throw new IllegalStateException("Unable to locate artifact for " + groupId + ":" + artifactId);
            }
            else
            {
               return new File(fileName);
            }
         }
      }
      else
      {
         return new File(matches.group(0));
      }
   }

   public static File resolve(String qualifiedArtifactId)
   {
      String[] segments = qualifiedArtifactId.split(":");
      return resolve(segments[0], segments[1]);
   }
   
   public static String findBuiltArtifact(List<String> targetPaths, String artifactId)
   {
      final String regex = "^" + artifactId + "-[\\d+\\.]+(?:\\-\\p{Upper}*)?.jar$";
      for (String targetPath : targetPaths)
      {
         File target = new File(targetPath);
         if (target.exists())
         {
            if (!target.isDirectory())
            {
               throw new IllegalStateException("Found ${project.dir}/target/ but it is not a directory!");
            }
            String[] possibleFiles = target.list(new FilenameFilter()
            {
               
               public boolean accept(File dir, String name)
               {
                  return name.matches(regex);
               }
               
            });
            if (possibleFiles.length == 1)
            {
               return "target" + File.separatorChar + possibleFiles[0];
            }
            if (possibleFiles.length > 0)
            {
               throw new IllegalStateException("Found multiple matching files " + Arrays.asList(possibleFiles) + " for " + artifactId + " not sure which one to choose");
            }
         }
      }
      return null;
   }
   
   public static Matcher getRepoMatcher(String groupId, String artifactId, String classPath, char pathSeparator, char fileSeparator)
   {
      String pathString = getPathString(groupId, artifactId, File.separatorChar);
      Pattern p = Pattern.compile("[^:]*" + Pattern.quote(pathString) + "[^:]*", Pattern.CASE_INSENSITIVE);
      return p.matcher(classPath);
   }
   
   public static Matcher getLocalClassesMatcher(String classPath, String localClasses, char pathSeparator)
   {
      Pattern p = Pattern.compile("[^" + pathSeparator + "]*" + localClasses + "[^" + pathSeparator + "]*", Pattern.CASE_INSENSITIVE);
      return p.matcher(classPath);
   }
   
   public static String getPathString(String groupId, String artifactId, char fileSeparator)
   {
      return groupId.replace('.', fileSeparator) + fileSeparator + artifactId;
   }
   
   public static String getLocalClassesString(char fileSeparator)
   {
      return Pattern.quote("target" + fileSeparator + "classes");
   }
}
