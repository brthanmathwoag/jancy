package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object Blockinfile extends SpecialCase {

  override def moduleName: String = "blockinfile"

  override def optionsWithNoEnum: Set[String] =
    Set(
      "insertbefore",
      "insertafter")
}
