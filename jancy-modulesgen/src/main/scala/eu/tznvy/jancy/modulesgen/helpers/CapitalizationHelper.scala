package eu.tznvy.jancy.modulesgen.helpers

/**
  * Converts variable/class/method/etc name capitalization
  */
object CapitalizationHelper {

  def snakeCaseToPascalCase(snakeCase: String): String =
    snakeCase
      .split('_')
      .map(_.capitalize)
      .mkString

  def snakeCaseToCamelCase(snakeCase: String): String = {
    val pascalCase = snakeCaseToPascalCase(snakeCase)
    //TODO: a faster way maybe
    pascalCase.head.toLower + pascalCase.tail
  }
}
