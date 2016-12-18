package jancy.transpiler.rendering

import jancy.core.Task

import scala.collection.JavaConverters._
import scala.collection.mutable.LinkedHashMap

object TaskRenderer {

  def render(task: Task): String = {
    YamlContext.get.dump(buildModel(task).asJava)
  }

  def render(tasks: Seq[Task]): String =
    YamlContext.get.dump(tasks.map(buildModel(_).asJava).toArray)

  def buildModel(task: Task): LinkedHashMap[String, Any] = {
    val taskArguments = task.getArguments.asScala.toMap
    //TODO: can throw in the future
    val name = taskArguments("name")
    val otherArguments = taskArguments - "name"

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

    val orderedPairs = Seq(
      "name" -> name,
      moduleName -> actionArgumentsString
    ) ++ otherArguments.toList.sorted

    LinkedHashMap[String, Any](orderedPairs: _*)
  }
}
