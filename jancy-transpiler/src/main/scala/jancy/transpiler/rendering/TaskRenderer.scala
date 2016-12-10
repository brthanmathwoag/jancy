package jancy.transpiler.rendering

import jancy.core.Task
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import scala.collection.JavaConverters._

object TaskRenderer {

  def render(task: Task): String = {
    val modifiers = task.getModifiers.asScala

    val argumentsString =
      task
        .getActionArguments
        .asScala
        .toList
        .sorted
        .map({ case (k, v) => s"$k='$v'" })
        .mkString("\n")

    val model = modifiers + (task.getModuleName -> argumentsString)

    yaml.dump(model.asJava)
  }

  lazy val yaml: Yaml = {
    val dumperOptions = new DumperOptions()
    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    new Yaml(dumperOptions)
  }
}
