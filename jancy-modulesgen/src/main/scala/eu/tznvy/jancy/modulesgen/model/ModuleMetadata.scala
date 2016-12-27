package eu.tznvy.jancy.modulesgen.model

case class ModuleMetadata(
  className: String,
  originalName: String,
  namespace: String,
  description: Option[String],
  shortDescription: Option[String],
  options: Seq[OptionMetadata]
)