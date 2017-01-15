package eu.tznvy.jancy.modulesgen.specialcases

import eu.tznvy.jancy.modulesgen.model.Choice

trait SpecialCase {
  def moduleName: String
  def optionsWithNoEnum: Set[String] = Set()
  def optionsWithCustomEnumBuilder: Map[String, Seq[String] => Seq[Choice]] = Map()
}
