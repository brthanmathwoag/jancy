package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.Playbook
import eu.tznvy.jancy.transpiler.helpers.ArraysHelper

import scala.collection.JavaConverters._
import scala.collection.mutable

/**
  * Creates YAML representation of Playbooks
  */
object PlaybookRenderer extends Renderer[Playbook] {

  override def render(playbook: Playbook): String =
    YamlContext.get.dump(buildModel(playbook).asJava)

  override def renderAll(playbooks: Array[Playbook]): String = {
    val model = playbooks.map(buildModel(_).asJava).toArray
    YamlContext.get.dump(model)
  }

  private def buildModel(playbook: Playbook): mutable.LinkedHashMap[String, Any] = {
    val orderedPairs = "name" -> playbook.getName :: List[(String, Any)](
        "hosts" -> ArraysHelper.flattenAnArray(playbook.getHosts),
        "roles" -> playbook.getRoles,
        "tasks" -> playbook.getTasks.map(new TasklikeRenderer().buildModel(_).asJava),
        "handlers" -> playbook.getHandlers.map(new TasklikeRenderer().buildModel(_).asJava))
      .filter((p) => !ArraysHelper.isAnEmptyArray(p._2))
      .sortBy(_._1)

    mutable.LinkedHashMap[String, Any](orderedPairs: _*)
  }
}
