package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.Playbook

import scala.collection.JavaConverters._

object PlaybookRenderer {

  def render(playbook: Playbook): String =
    YamlContext.get.dump(buildModel(playbook).asJava)

  def render(playbooks: Seq[Playbook]): String = {
    val model = playbooks.map(buildModel(_).asJava).toArray
    YamlContext.get.dump(model)
  }

  private def buildModel(playbook: Playbook): Map[String, Any] =
    Map(
      "hosts" -> playbook.getHosts,
      "name" -> playbook.getName,
      "roles" -> playbook.getRoles)
}
