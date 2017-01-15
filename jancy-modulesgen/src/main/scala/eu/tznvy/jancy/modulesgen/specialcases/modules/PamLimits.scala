package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.helpers.CapitalizationHelper
import eu.tznvy.jancy.modulesgen.model.Choice
import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object PamLimits extends SpecialCase {

  override def moduleName: String = "pam_limits"

  override def optionsWithCustomEnumBuilder = Map(
    "limit_type" -> makeAnEnum)

  private def makeAnEnum(choices: Seq[String]): Seq[Choice] =
    choices.map({ c =>
      val name = if (c == "-") "none" else c
      Choice(CapitalizationHelper.snakeCaseToAllCaps(name), c)
    })
}

