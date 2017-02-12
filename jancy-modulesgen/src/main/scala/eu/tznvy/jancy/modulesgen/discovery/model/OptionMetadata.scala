package eu.tznvy.jancy.modulesgen.discovery.model

/**
  * Represents module's parameter
  *
  * @param name           the name in camel case
  * @param originalName   the name in snake case
  * @param isRequired     is the parameter required to be set on invocation
  * @param description    the long description
  * @param default        the default value
  * @param choices        the available values
  * @param aliases        the aliases for this parameter
  */
case class OptionMetadata(
  name: String,
  originalName: String,
  isRequired: Boolean,
  description: Option[String],
  default: Option[String],
  choices: Option[Choices],
  aliases: Seq[OptionAliasMetadata]
)