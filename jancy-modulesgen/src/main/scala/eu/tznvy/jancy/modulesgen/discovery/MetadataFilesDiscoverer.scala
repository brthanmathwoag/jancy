package eu.tznvy.jancy.modulesgen.discovery

import java.io.File

import scala.io.Source

import resource._

/***
  * Picks Ansible module files in a particular path
  */
object MetadataFilesDiscoverer {

  def discoverFiles(path: String): Seq[File] =
    getAllFiles(path).filter(isModuleFile)

  private def getAllFiles(path: String): Seq[File] = {
    //TODO: @tailrec
    def getTree(f: File): Stream[File] =
      if (f.isDirectory) f.listFiles.toStream.flatMap(getTree)
      else Stream(f)

    getTree(new File(path))
  }

  private def isModuleFile(file: File): Boolean = {
    def hasPythonExtension: Boolean = file.getPath.endsWith(".py")

    def containsModuleDefinition: Boolean =
      managed(Source.fromFile(file))
        .map(_.getLines.exists(_.contains("module = AnsibleModule(")))
        .opt
        .getOrElse(false)

    def containsVirtualModuleHeader =
      managed(Source.fromFile(file))
        .map(_.getLines.exists(_.startsWith("# this is a virtual module that is entirely implemented server side")))
        .opt
        .getOrElse(false)

    hasPythonExtension && (containsModuleDefinition || containsVirtualModuleHeader)
  }
}
