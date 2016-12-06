package jancy.modulesgen.model

case class OptionMetadata(
  originalName: String,
  isRequired: Boolean,
  description: Option[String],
  default: Option[String],
  choices: Seq[String],
  name: String
)