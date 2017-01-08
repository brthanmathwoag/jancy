package eu.tznvy.jancy.modulesgen.model

/**
  * Represents an alias for a module's parameter
  *
  * @param name           the name of the alias
  * @param originalName   the name of the aliased parameter
  */
case class OptionAliasMetadata(
  name: String,
  originalName: String
)
