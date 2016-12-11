package jancy.transpiler.rendering

import jancy.core.Task
import org.yaml.snakeyaml.{DumperOptions, Yaml}
import scala.collection.JavaConverters._

object TaskRenderer {

  def render(task: Task): String = {
    val taskArguments = task.getArguments.asScala
    //TODO: can throw
    val moduleName = task.getAction.get.getModuleName

    val actionArgumentsString =
      task
        .getAction
        //TODO: can throw
        .get
        .getArguments
        .asScala
        .toList
        .sorted
        .map({ case (k, v) => s"$k='$v'" })
        .mkString("\n")

    val model = taskArguments + (moduleName -> actionArgumentsString)

    yaml.dump(model.asJava)
  }

  lazy val yaml: Yaml = {
    val dumperOptions = new DumperOptions()
    dumperOptions.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
    new Yaml(dumperOptions)
  }
}
