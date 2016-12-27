package eu.tznvy.jancy.modulesgen.model

case class OptionMetadata(
  name: String,
  originalName: String,
  isRequired: Boolean,
  description: Option[String],
  default: Option[String],
  choices: Seq[String],
  aliases: Seq[OptionAliasMetadata]
)