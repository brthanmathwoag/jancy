package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.discovery.model.Choice
import eu.tznvy.jancy.modulesgen.helpers.CapitalizationHelper
import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object Ec2MetricAlarm extends SpecialCase {

  override def moduleName: String = "ec2_metric_alarm"

  override def optionsWithCustomEnumBuilder = Map(
    "comparison" -> makeAnEnum)

  private def makeAnEnum(choices: Seq[String]): Seq[Choice] =
    choices.map({
      case ">" => Choice("GT", ">")
      case "<=" => Choice("LE", "<=")
      case "<" => Choice("LT", "<")
      case ">=" => Choice("GE", ">=")
      case c => Choice(CapitalizationHelper.snakeCaseToAllCaps(c), c)
    })
}