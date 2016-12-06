package jancy.modulesgen

import jancy.modulesgen.discovery.{MetadataReader, MetadataFilesDiscoverer}
import jancy.modulesgen.codegeneration.ClassGenerator

import java.nio.file.Paths

object Main {

  def main(args: Array[String]): Unit = {

    val ansibleModulesPaths = List(
        "submodules/ansible-modules-core",
        "submodules/ansible-modules-extras")

    val outputJavaClassesPath = Paths.get("jancy-modules/src/main/java/")
    outputJavaClassesPath.toFile.delete()

    ansibleModulesPaths
      .flatMap(MetadataFilesDiscoverer.discoverFiles)
      .map(MetadataReader.readModuleMetadata)
      .foreach(ClassGenerator.generateClass(outputJavaClassesPath, _))
  }
}
