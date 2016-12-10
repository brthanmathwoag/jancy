package jancy.modulesgen

import jancy.modulesgen.discovery.{MetadataReader, MetadataFilesDiscoverer}
import jancy.modulesgen.codegeneration.{ClassGenerator, FilesLayout}

import java.nio.file.Paths

object Main {

  def main(args: Array[String]): Unit = {

    val ansibleModulesPaths = List(
        "submodules/ansible-modules-core",
        "submodules/ansible-modules-extras")

    val filesLayout = new FilesLayout(
      Paths.get("jancy-modules/src/main/java/"))

    ansibleModulesPaths
      .flatMap(MetadataFilesDiscoverer.discoverFiles)
      .map(MetadataReader.readModuleMetadata)
      .map({ m => (m, ClassGenerator.generateClass(m)) })
      .foreach((filesLayout.saveModuleSource _).tupled)
  }
}
