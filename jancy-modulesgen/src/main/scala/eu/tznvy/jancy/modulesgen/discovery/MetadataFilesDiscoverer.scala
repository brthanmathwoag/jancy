package eu.tznvy.jancy.modulesgen.discovery

import java.io.File
import scala.annotation.tailrec
import scala.io.Source

import resource._

/**
  * Picks Ansible module files in a particular path
  */
object MetadataFilesDiscoverer {

  def discoverFiles(path: String): Seq[File] =
    getAllFiles(path).filter(isModuleFile)

  private def getAllFiles(path: String): Seq[File] = {
    @tailrec
    def loop(toVisit: List[File], resultSoFar: List[File]): List[File] =
      toVisit match {
        case Nil => resultSoFar
        case f :: fs if f.isDirectory => loop(f.listFiles.toList ::: fs, resultSoFar)
        case f :: fs if !f.isDirectory => loop(fs, f :: resultSoFar)
      }

    loop(List(new File(path)), List())
  }

  private def isModuleFile(file: File): Boolean = {
    val virtualModuleHeader =
      "# this is a virtual module that is entirely implemented server side"

    val moduleConstructorInvocation = "module = AnsibleModule("

    def hasPythonExtension: Boolean = file.getPath.endsWith(".py")

    def containsModuleDefinition: Boolean =
      managed(Source.fromFile(file))
        .map(_.getLines.exists(_.contains(moduleConstructorInvocation)))
        .opt
        .getOrElse(false)

    def containsVirtualModuleHeader =
      managed(Source.fromFile(file))
        .map(_
            .getLines
            .exists(_
              .startsWith(virtualModuleHeader)))
        .opt
        .getOrElse(false)

    hasPythonExtension && (containsModuleDefinition || containsVirtualModuleHeader)
  }
}
