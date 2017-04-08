package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object CnosPortchannel extends SpecialCase{

  override def moduleName: String = "cnos_portchannel"

  override def optionsWithNoEnum: Set[String] = Set(
    "interfaceArg1",
    "interfaceArg2",
    "interfaceArg3",
    "interfaceArg4",
    "interfaceArg5",
    "interfaceArg6",
    "interfaceArg7")
}
