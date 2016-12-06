package jancy.modulesgen.discovery

import java.io.File

import scala.io.Source

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
    //TODO: resource mgmt
      Source
        .fromFile(file)
        .getLines
        .exists(_.contains("module = AnsibleModule("))

    hasPythonExtension && containsModuleDefinition
  }
}
