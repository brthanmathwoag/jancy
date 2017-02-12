package eu.tznvy.jancy.modulesgen.discovery.model

/**
  * Represents available option value
  *
  * @param name           the name in all caps
  * @param originalName   the name in snake case
  */
case class Choice(
  name: String,
  originalName: String
)