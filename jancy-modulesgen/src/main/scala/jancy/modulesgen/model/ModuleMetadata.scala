package jancy.modulesgen.model

case class ModuleMetadata(
  originalName: String,
  className: String,
  namespace: String,
  description: Option[String],
  shortDescription: Option[String],
  options: Seq[OptionMetadata]
)