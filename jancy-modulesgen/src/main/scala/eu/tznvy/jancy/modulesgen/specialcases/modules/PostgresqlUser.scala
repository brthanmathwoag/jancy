package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.helpers.CapitalizationHelper
import eu.tznvy.jancy.modulesgen.discovery.model.Choice
import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object PostgresqlUser extends SpecialCase {
  override def moduleName: String = "postgresql_user"

  override def optionsWithCustomEnumBuilder = Map(
    "role_attr_flags" -> makeAnEnum)

  private def makeAnEnum(choices: Seq[String]): Seq[Choice] =
    choices
      .flatMap({ c =>
        if (c.startsWith("[NO]")) {
          val name = c.substring(4)
          Seq(name, "NO" + name)
        } else Seq(c)
      }).map({ c =>
      Choice(
        CapitalizationHelper.snakeCaseToAllCaps(c),
        c)
    })
}
