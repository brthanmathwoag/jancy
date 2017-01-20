package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.Path
import java.util

import scala.collection.JavaConverters._
import eu.tznvy.jancy.core.{Playbook, Inventory, Play, Role}
import eu.tznvy.jancy.transpiler.helpers.Filesystem

/**
  * Creates YAML representation of Playbooks on the provided filesystem
  *
  * @param filesystem     a wrapper for IO operations
  */
class PlaybookRenderer(filesystem: Filesystem) {

  def render(playbook: Playbook, root: Path): Unit = {
    filesystem.createDirectories(root)

    playbook
      .getInventories
      .map({ i => (makeInventoryPath(root, i), InventoryRenderer.render(i)) })
      .foreach({ case (p, c) => filesystem.writeFile(p, c) })

    //TODO: these should probably be rendered by InventoryRenderer
    saveVars(
      root.resolve("host_vars"),
      playbook
        .getInventories
        .flatMap(_.getHosts)
        .map({ h => (h.getName, h.getVars) }))

    saveVars(
      root.resolve("group_vars"),
      playbook
        .getInventories
        .flatMap(_.getGroups)
        .map({ g => (g.getName, g.getVars) }))

    saveVars(
      root.resolve("group_vars"),
      playbook
        .getInventories
        .map({ i => ("all", i.getVars )}))

    filesystem.writeFile(
      makeMainPlaybookPath(root),
      PlayRenderer.renderAll(playbook.getPlays))

    val roleRenderer = new RoleRenderer(filesystem)

    playbook.getRoles
        .foreach({ r => roleRenderer.render(r, makeRolePath(root, r)) })
  }

  private def makeInventoryPath(root: Path, inventory: Inventory): Path =
    root.resolve(inventory.getName)

  private def makeMainPlaybookPath(root: Path): Path =
    root.resolve("site.yml")

  private def makePlaybookPath(root: Path, playbook: Play): Path =
    root.resolve(playbook.getName + ".yml")

  private def makeRolePath(root: Path, role: Role): Path =
    root.resolve("roles").resolve(role.getName)

  private def saveVars(rootPath: Path, vars: Array[(String, util.Map[String, AnyRef])]): Unit = {
    vars
      .groupBy({ p => p._1 })
      .map({ g =>
        val pairs = g._2
          .flatMap({ p => p._2.asScala.toSeq })
          .toArray[(String, Any)]
        val content = VarsRenderer.renderAll(pairs)
        val path = rootPath.resolve(g._1)
        val isEmpty = pairs.length == 0
        (path, content, isEmpty)
      })
      .filter(!_._3)
      .foreach({ case (p, c, _) => filesystem.writeFile(p, c) })
  }
}
