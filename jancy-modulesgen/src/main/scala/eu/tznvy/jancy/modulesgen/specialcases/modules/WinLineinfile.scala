package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object WinLineinfile extends SpecialCase {

  override def moduleName: String = "win_lineinfile"

  override def optionsWithNoEnum: Set[String] =
    Set(
      "insertbefore",
      "insertafter")
}

