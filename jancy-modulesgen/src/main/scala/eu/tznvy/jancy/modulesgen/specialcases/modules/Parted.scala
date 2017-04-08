package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.helpers.CapitalizationHelper
import eu.tznvy.jancy.modulesgen.discovery.model.Choice
import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object Parted extends SpecialCase {

  override def moduleName: String = "parted"

  override def optionsWithCustomEnumBuilder = Map(
    "unit" -> makeAnEnum)

  private def makeAnEnum(choices: Seq[String]): Seq[Choice] =
    choices.map({
      case "%" => Choice("PERCENTS", "%")
      case c => Choice(CapitalizationHelper.snakeCaseToAllCaps(c), c)
    })
}