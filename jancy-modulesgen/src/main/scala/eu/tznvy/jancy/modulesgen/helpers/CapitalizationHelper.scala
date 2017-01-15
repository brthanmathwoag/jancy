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

  def camelCaseToPascalCase(camelCase: String): String =
    camelCase.head.toUpper + camelCase.tail

  def snakeCaseToAllCaps(snakeCase: String): String =
    snakeCase.map(_.toUpper)
}
