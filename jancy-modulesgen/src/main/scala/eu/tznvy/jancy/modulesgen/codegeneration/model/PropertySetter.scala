package eu.tznvy.jancy.modulesgen.codegeneration.model

class PropertySetter(
  val name: String,
  val originalName: String,
  val javadoc: String,
  val className: String,
  val isDeprecated: Boolean
) {
  val setterType: String = getClass.getSimpleName
}
