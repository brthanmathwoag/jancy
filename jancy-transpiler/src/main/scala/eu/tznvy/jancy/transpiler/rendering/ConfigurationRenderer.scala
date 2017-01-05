package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.Path
import java.util

import scala.collection.JavaConverters._
import eu.tznvy.jancy.core.{Configuration, Inventory, Playbook, Role}
import eu.tznvy.jancy.transpiler.helpers.Filesystem

class ConfigurationRenderer(filesystem: Filesystem) {

  def render(configuration: Configuration, root: Path): Unit = {
    filesystem.createDirectories(root)

    configuration
      .getInventories
      .map({ i => (makeInventoryPath(root, i), InventoryRenderer.render(i)) })
      .foreach({ case (p, c) => filesystem.writeFile(p, c) })

    saveVars(
      root.resolve("host_vars"),
      configuration
        .getInventories
        .flatMap(_.getHosts)
        .map({ h => (h.getName, h.getVars) }))

    saveVars(
      root.resolve("group_vars"),
      configuration
        .getInventories
        .flatMap(_.getGroups)
        .map({ g => (g.getName, g.getVars) }))

    saveVars(
      root.resolve("group_vars"),
      configuration
        .getInventories
        .map({ i => ("all", i.getVars )}))

    filesystem.writeFile(
      makeMainPlaybookPath(root),
      PlaybookRenderer.render(configuration.getPlaybooks))

    val roleRenderer = new RoleRenderer(filesystem)

    configuration.getRoles
        .foreach({ r => roleRenderer.render(r, makeRolePath(root, r)) })
  }

  private def makeInventoryPath(root: Path, inventory: Inventory): Path =
    root.resolve(inventory.getName)

  private def makeMainPlaybookPath(root: Path): Path =
    root.resolve("site.yml")

  private def makePlaybookPath(root: Path, playbook: Playbook): Path =
    root.resolve(playbook.getName + ".yml")

  private def makeRolePath(root: Path, role: Role): Path =
    root.resolve("roles").resolve(role.getName)

  private def saveVars(rootPath: Path, vars: Array[(String, util.Map[String, AnyRef])]): Unit = {
    filesystem.createDirectories(rootPath)

    vars
      .groupBy({ p => p._1 })
      .map({ g =>
        val pairs = g._2
          .flatMap({ p => p._2.asScala.toSeq }).toMap
        val content = VarsRenderer.render(pairs)
        val path = rootPath.resolve(g._1)
        (path, content)
      })
      .foreach({ case (p, c) => filesystem.writeFile(p, c) })
  }
}
