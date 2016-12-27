package eu.tznvy.jancy.transpiler.rendering

import org.yaml.snakeyaml.{DumperOptions, Yaml}


object YamlContext {

  lazy val get: Yaml = {
    val dumperOptions = new DumperOptions()
    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    new Yaml(dumperOptions)
  }
}
