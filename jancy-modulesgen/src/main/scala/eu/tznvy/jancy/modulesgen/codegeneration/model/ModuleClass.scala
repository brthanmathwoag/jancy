package eu.tznvy.jancy.modulesgen.codegeneration.model

case class ModuleClass(
  name: String,
  originalName: String,
  namespace: String,
  javadoc: String,
  setters: Array[PropertySetter],
  enums: Array[PropertyEnum],
  isFreeform: Boolean,
  isDeprecated: Boolean
)