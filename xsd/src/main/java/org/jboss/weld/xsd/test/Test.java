package org.jboss.weld.xsd.test;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.processing.AbstractProcessor;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import javax.tools.JavaCompiler.CompilationTask;

import org.jboss.weld.xsd.PackageSchemaGenerator;

public class Test
{

   public static void main(String[] args)
   {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
      Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjects(args);
      CompilationTask task = compiler.getTask(null, fileManager, null, null, null, compilationUnits);
      List<AbstractProcessor> processors = new ArrayList<AbstractProcessor>();
      processors.add(new PackageSchemaGenerator());
      task.setProcessors(processors);
      task.call();
   }

}
