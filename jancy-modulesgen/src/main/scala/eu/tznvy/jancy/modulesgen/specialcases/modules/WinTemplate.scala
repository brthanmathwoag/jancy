package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.discovery.model.Choice
import eu.tznvy.jancy.modulesgen.helpers.CapitalizationHelper
import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object WinTemplate extends SpecialCase {

  override def moduleName: String = "win_template"

  override def optionsWithCustomEnumBuilder = Map(
    "newline_sequence" -> makeAnEnum)

  private def makeAnEnum(choices: Seq[String]): Seq[Choice] =
    choices.map({
      case "\\n" => Choice("UNIX", "\\\\n")
      case "\\r\\n" => Choice("DOS", "\\\\r\\\\n")
      case "\\r" => Choice("MAC", "\\\\r")
      case c => Choice(CapitalizationHelper.snakeCaseToAllCaps(c), c)
    })
}