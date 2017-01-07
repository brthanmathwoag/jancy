package eu.tznvy.jancy.modulesgen.codegeneration

import java.nio.file.{Files, Paths, Path, FileSystems}

import eu.tznvy.jancy.modulesgen.model.ModuleMetadata

class FilesLayout(srcRootPath: Path) {
  def saveModuleSource(moduleMetadata: ModuleMetadata, content: String): Unit = {
    val outputDirectory = srcRootPath.resolve(moduleMetadata.namespace.replace(".", FileSystems.getDefault.getSeparator))
    outputDirectory.toFile.mkdirs()

    val outputFile = outputDirectory.resolve(moduleMetadata.className + ".java")
    Files.write(outputFile, content.getBytes)
  }
}
