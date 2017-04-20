package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.discovery.model.Choice
import eu.tznvy.jancy.modulesgen.helpers.CapitalizationHelper
import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object DimensiondataNetwork extends SpecialCase {

  override def moduleName: String = "dimensiondata_network"

  override def optionsWithCustomEnumBuilder = Map(
    "region" -> makeAnEnum)

  private def makeAnEnum(choices: Seq[String]): Seq[Choice] =
    Seq(
      Choice("NORTH_AMERICA", "na"),
      Choice("EUROPE", "eu"),
      Choice("AFRICA", "af"),
      Choice("AUSTRALIA", "au"),
      Choice("AUSTRALIA_ACT", "au-gov"),
      Choice("LATIN_AMERICA", "latam"),
      Choice("ASIA_PACIFIC", "ap"),
      Choice("CANADA", "canada")
    )
}
