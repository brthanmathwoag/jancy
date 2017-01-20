package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.Play
import eu.tznvy.jancy.transpiler.helpers.ArraysHelper

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Creates YAML representation of Plays
  */
object PlayRenderer extends Renderer[Play] {

  override def render(play: Play): String =
    YamlContext.get.dump(buildModel(play).asJava)

  override def renderAll(plays: Array[Play]): String = {
    val model = plays.map(buildModel(_).asJava).toArray
    YamlContext.get.dump(model)
  }

  private def buildModel(play: Play): mutable.LinkedHashMap[String, Any] = {
    val orderedPairs = "name" -> play.getName :: List[(String, Any)](
        "hosts" -> ArraysHelper.flattenAnArray(play.getHosts),
        "roles" -> play.getRoles,
        "tasks" -> play.getTasks.map(new TasklikeRenderer().buildModel(_).asJava),
        "handlers" -> play.getHandlers.map(new TasklikeRenderer().buildModel(_).asJava))
      .filter((p) => !ArraysHelper.isAnEmptyArray(p._2))
      .sortBy(_._1)

    mutable.LinkedHashMap[String, Any](orderedPairs: _*)
  }
}
