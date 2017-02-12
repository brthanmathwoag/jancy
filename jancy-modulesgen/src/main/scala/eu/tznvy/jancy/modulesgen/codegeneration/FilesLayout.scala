package eu.tznvy.jancy.modulesgen.codegeneration

import java.nio.file.{Files, Paths, Path}

import eu.tznvy.jancy.modulesgen.discovery.model.ModuleMetadata

/**
  * Saves module wrapper sourcecode in an appropriate package
  * @param srcRootPath root sources path, e.g. jancy-modules/src/main/java
  */
class FilesLayout(srcRootPath: Path) {
  //TODO: split path resolving logic and IO in two classes
  def saveModuleSource(moduleMetadata: ModuleMetadata, content: String): Unit = {
    val pathComponents = moduleMetadata.namespace.split('.')
    val outputDirectory = Paths.get(srcRootPath.toString, pathComponents: _*)
    val outputFile = outputDirectory.resolve(moduleMetadata.className + ".java")

    Files.createDirectories(outputDirectory)
    Files.write(outputFile, content.getBytes)
  }
}
