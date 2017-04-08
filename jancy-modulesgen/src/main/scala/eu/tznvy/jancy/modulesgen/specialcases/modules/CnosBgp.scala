package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object CnosBgp extends SpecialCase{

  override def moduleName: String = "cnos_bgp"

  override def optionsWithNoEnum: Set[String] = Set(
    "bgpArg1",
    "bgpArg2",
    "bgpArg3",
    "bgpArg4",
    "bgpArg5",
    "bgpArg6",
    "bgpArg7",
    "bgpArg8")
}
