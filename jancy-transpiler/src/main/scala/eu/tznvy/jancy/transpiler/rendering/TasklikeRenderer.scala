package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.Tasklike
import eu.tznvy.jancy.transpiler.helpers.ArraysHelper

import scala.collection.JavaConverters._
import scala.collection.mutable

class TasklikeRenderer[T <: Tasklike] extends Renderer[T] {

  override def render(tasklike: T): String = {
    YamlContext.get.dump(buildModel(tasklike).asJava)
  }

  override def renderAll(tasklikes: Array[T]): String =
    YamlContext.get.dump(tasklikes.map(buildModel(_).asJava).toArray)

  def buildModel(tasklike: T): mutable.LinkedHashMap[String, Any] = {
    val taskArguments = tasklike.getArguments.asScala.toMap
    //TODO: can throw in the future
    val name = taskArguments("name")

    //TODO: can throw
    val moduleName = tasklike.getAction.get.getModuleName

    val actionArguments =
      tasklike
        .getAction
        //TODO: can throw
        .get
        .getArguments
        .asScala
        .toMap

    val freeform = actionArguments.get("free_form")

    val otherActionArgumentsString =
      (actionArguments - "free_form")
        .toList
        .sorted
        .map({ case (k, v) => s"$k='$v'" })
        .mkString("\n")

    val actionArgumentsString =
      freeform
        .map(_ + "\n" + otherActionArgumentsString)
        .getOrElse(otherActionArgumentsString)

    val otherArguments = (taskArguments - "name")
      .map({case (k, v) => (k, ArraysHelper.flattenAPotentialArray(v))})
      .toList
      .sortBy(_._1)

    val orderedPairs = Seq(
      "name" -> name,
      moduleName -> actionArgumentsString
    ) ++ otherArguments

    mutable.LinkedHashMap[String, Any](orderedPairs: _*)
  }
}
