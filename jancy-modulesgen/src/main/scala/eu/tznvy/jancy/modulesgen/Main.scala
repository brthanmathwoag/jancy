package eu.tznvy.jancy.modulesgen

import eu.tznvy.jancy.modulesgen.helpers.Filesystem
import eu.tznvy.jancy.modulesgen.discovery.{MetadataReader, MetadataFilesDiscoverer}
import eu.tznvy.jancy.modulesgen.codegeneration.{ClassGenerator, FilesLayout}
import resource._
import java.nio.file.Paths
import scala.io.Source

object Main {

  def main(args: Array[String]): Unit = {

    val ansibleModulesPaths = List(
        "submodules/ansible-modules-core",
        "submodules/ansible-modules-extras")

    val filesLayout = new FilesLayout(
      Paths.get("jancy-modules/src/main/java/"))

    ansibleModulesPaths
      .flatMap(Filesystem.getFilesRecursively)
      .filter({ f =>
        managed(Source.fromFile(f))
          .map(MetadataFilesDiscoverer.isAnsibleModuleFile(f.getName, _))
          .opt
          .getOrElse(false)
       })
      .map(MetadataReader.readModuleMetadata)
      .map({ m => (m, ClassGenerator.generateClass(m)) })
      .foreach((filesLayout.saveModuleSource _).tupled)
  }
}
