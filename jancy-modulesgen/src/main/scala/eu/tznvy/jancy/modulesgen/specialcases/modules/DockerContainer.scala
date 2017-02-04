package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.helpers.CapitalizationHelper
import eu.tznvy.jancy.modulesgen.model.Choice
import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object DockerContainer extends SpecialCase {
  override def moduleName: String = "docker_container"

  override def optionsWithCustomEnumBuilder =
    Map("network_mode" -> makeAnEnum)

  private def makeAnEnum(choices: Seq[String]): Seq[Choice] =
    choices
        .filter(!_.startsWith("container:"))
      .map({ c =>
        Choice(
          CapitalizationHelper.snakeCaseToAllCaps(c),
          c)
      })
}
