package jancy.transpiler.rendering

import jancy.core.Task

import scala.collection.JavaConverters._

object TaskRenderer {

  def render(task: Task): String = {
    YamlContext.get.dump(buildModel(task).asJava)
  }

  def render(tasks: Seq[Task]): String =
    YamlContext.get.dump(tasks.map(buildModel(_).asJava).toArray)

  def buildModel(task: Task): Map[String, Any] = {
    val taskArguments = task.getArguments.asScala.toMap
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

    taskArguments + (moduleName -> actionArgumentsString)
  }
}
