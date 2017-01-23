package eu.tznvy.jancy.modulesgen.discovery

import scala.io.Source

/**
  * Picks Ansible module files in a particular path
  */
object MetadataFilesDiscoverer {

  def isAnsibleModuleFile(name: String, source: Source): Boolean =
    hasPythonExtension(name) && source.getLines.exists(lineSuggestsItsAModule)

  private def hasPythonExtension(name: String): Boolean =
    name.endsWith(".py")

  private def lineSuggestsItsAModule(line: String): Boolean =
    containsModuleConstructorInvocation(line) || containsVirtualModuleHeader(line)

  private val virtualModuleHeader =
    "# this is a virtual module that is entirely implemented server side"

  private def containsModuleConstructorInvocation(line: String): Boolean =
    line.contains(moduleConstructorInvocation)

  private val moduleConstructorInvocation = "module = AnsibleModule("

  private def containsVirtualModuleHeader(line: String): Boolean =
    line.startsWith(virtualModuleHeader)
}
