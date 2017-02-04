package eu.tznvy.jancy.modulesgen.discovery

import java.util.regex.Pattern

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
    containsModuleConstructorInvocation(line) || containsDocumentation(line)

  private val moduleConstructorInvocation = "module = AnsibleModule("

  private def containsModuleConstructorInvocation(line: String): Boolean =
    line.contains(moduleConstructorInvocation)

  private def containsDocumentation(line: String): Boolean =
    Pattern.matches("^DOCUMENTATION\\s*=\\s*[ur]?['\"]{3}.*", line)
}
