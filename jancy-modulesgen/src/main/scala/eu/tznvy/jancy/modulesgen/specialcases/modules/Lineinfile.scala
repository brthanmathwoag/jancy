package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object Lineinfile extends SpecialCase {

  override def moduleName: String = "lineinfile"

  override def optionsWithNoEnum: Set[String] =
    Set(
      "insertbefore",
      "insertafter")
}

