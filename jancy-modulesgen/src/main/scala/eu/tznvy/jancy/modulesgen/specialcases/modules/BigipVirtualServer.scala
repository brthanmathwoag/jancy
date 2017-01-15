package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object BigipVirtualServer extends SpecialCase {
  override def moduleName: String = "bigip_virtual_server"

  override def optionsWithNoEnum: Set[String] = Set("snat")
}
