package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object CnosVlan extends SpecialCase{

  override def moduleName: String = "cnos_vlan"

  override def optionsWithNoEnum: Set[String] = Set(
    "vlanArg1",
    "vlanArg2",
    "vlanArg3",
    "vlanArg4",
    "vlanArg5")
}
