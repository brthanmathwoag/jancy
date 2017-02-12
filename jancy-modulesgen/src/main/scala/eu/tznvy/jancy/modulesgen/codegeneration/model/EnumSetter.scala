package eu.tznvy.jancy.modulesgen.codegeneration.model

case class EnumSetter(
 override val name: String,
 override val originalName: String,
 override val javadoc: String,
 override val className: String,
 override val isDeprecated: Boolean,
 typeName: String
) extends PropertySetter(
  name, originalName, javadoc, className, isDeprecated
)