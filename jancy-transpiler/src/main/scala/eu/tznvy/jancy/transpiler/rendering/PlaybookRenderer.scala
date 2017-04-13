package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.Path
import java.util

import scala.collection.JavaConverters._
import eu.tznvy.jancy.core.{Playbook, Inventory, Play, Role}
import eu.tznvy.jancy.transpiler.helpers.{ Filesystem, GroupsHelper }

/**
  * Creates YAML representation of Playbooks on the provided filesystem
  *
  * @param filesystem     a wrapper for IO operations
  */
class PlaybookRenderer(filesystem: Filesystem) {

  def render(playbook: Playbook, root: Path): Unit = {
    filesystem.createDirectories(root)

    val multiinventory = playbook.getInventories.length > 1

    playbook
      .getInventories
      .map({ i => (makeInventoryPath(root, i, multiinventory), InventoryRenderer.render(i)) })
      .foreach({ case (p, c) => filesystem.writeFile(p, c) })

    //TODO: these should probably be rendered by InventoryRenderer
    playbook
      .getInventories
      .foreach({ i =>
        val allHosts =
          GroupsHelper
            .flattenSubgroups(i.getGroups)
            .flatMap(_.getHosts) ++ i.getHosts

        val varsPerHost = allHosts
          .groupBy(_.getName)
          .map({ g => (
            g._1,
            g._2
              .flatMap(_.getVars.asScala.toSet)
              .toMap) })
          .toArray

        val inventoryDirectory = makeInventoryPath(root, i, multiinventory).getParent

        saveVars(
          inventoryDirectory.resolve("host_vars"),
          varsPerHost)

        saveVars(
          inventoryDirectory.resolve("group_vars"),
          GroupsHelper
            .flattenSubgroups(i.getGroups)
            .toArray
            .map({ g => (g.getName, g.getVars.asScala.toMap) })
        )

        saveVars(
          inventoryDirectory.resolve("group_vars"),
          Array(("all", i.getVars.asScala.toMap))
        )
      })

    filesystem.writeFile(
      makeMainPlaybookPath(root),
      PlayRenderer.renderAll(playbook.getPlays))

    val roleRenderer = new RoleRenderer(filesystem)

    playbook.getRoles
      .foreach({ r => roleRenderer.render(r, makeRolePath(root, r)) })
  }

  private def makeInventoryPath(root: Path, inventory: Inventory, multiinventory: Boolean): Path =
    if (multiinventory) root.resolve("inventories").resolve(inventory.getName).resolve("hosts")
    else root.resolve(inventory.getName)

  private def makeMainPlaybookPath(root: Path): Path =
    root.resolve("site.yml")

  private def makePlaybookPath(root: Path, playbook: Play): Path =
    root.resolve(playbook.getName + ".yml")

  private def makeRolePath(root: Path, role: Role): Path =
    root.resolve("roles").resolve(role.getName)

  private def saveVars(rootPath: Path, vars: Array[(String, Map[String, AnyRef])]): Unit = {
    vars
      .groupBy({ p => p._1 })
      .map({ g =>
        val pairs = g._2
          .flatMap({ p => p._2.toSeq })
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
