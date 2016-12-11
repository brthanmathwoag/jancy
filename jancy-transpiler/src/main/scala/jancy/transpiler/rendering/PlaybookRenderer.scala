package jancy.transpiler.rendering

import jancy.core.Playbook

import scala.collection.JavaConverters._

object PlaybookRenderer {

  def render(playbook: Playbook): String = {
    val model = Map(
      "hosts" -> playbook.getHosts,
      "name" -> playbook.getName,
      "roles" -> playbook.getRoles)

    YamlContext.get.dump(model.asJava)
  }
}
