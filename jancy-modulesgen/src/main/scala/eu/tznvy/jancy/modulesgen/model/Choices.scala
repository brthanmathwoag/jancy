package eu.tznvy.jancy.modulesgen.model

/**
  * Represents available option values
  *
  * @param name           the enum name in pascal case
  * @param choices        the available values
  */
case class Choices(
  name: String,
  choices: Seq[Choice]
)