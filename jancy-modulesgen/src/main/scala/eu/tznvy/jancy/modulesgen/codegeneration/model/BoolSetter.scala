package eu.tznvy.jancy.modulesgen.codegeneration.model

case class BoolSetter(
 override val name: String,
 override val originalName: String,
 override val javadoc: String,
 override val className: String,
 override val isDeprecated: Boolean,
 trueValue: String,
 falseValue: String
) extends PropertySetter(
  name, originalName, javadoc, className, isDeprecated
)
