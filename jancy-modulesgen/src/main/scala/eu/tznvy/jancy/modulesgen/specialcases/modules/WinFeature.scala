package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object WinFeature extends SpecialCase {

  override def moduleName: String = "win_feature"

  override def optionsWithNoEnum: Set[String] = Set("source")
}
