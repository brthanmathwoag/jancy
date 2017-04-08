package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object CnosInterface extends SpecialCase{

  override def moduleName: String = "cnos_interface"

  override def optionsWithNoEnum: Set[String] = Set(
    "interfaceArg1",
    "interfaceArg2",
    "interfaceArg3",
    "interfaceArg4",
    "interfaceArg5",
    "interfaceArg6",
    "interfaceArg7")
}
