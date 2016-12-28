package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.{FileSystems, Files, Path}
import java.util

import scala.collection.JavaConverters._
import eu.tznvy.jancy.core.{Configuration, Inventory, Playbook, Role}

object ConfigurationRenderer {

  def render(configuration: Configuration, root: Path): Unit = {
    root.toFile.mkdirs()

    configuration
      .getInventories
      .map({ i => (makeInventoryPath(root, i), InventoryRenderer.render(i)) })
      .foreach({ case (p, c) => Files.write(p, c.getBytes) })

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

    Files.write(
      makeMainPlaybookPath(root),
      PlaybookRenderer.render(configuration.getPlaybooks).getBytes)

    configuration.getRoles
        .foreach({ r => RoleRenderer.render(r, makeRolePath(root, r)) })
  }

  private def makeInventoryPath(root: Path, inventory: Inventory): Path =
    root.resolve(inventory.getName)

  private def makeMainPlaybookPath(root: Path): Path =
    root.resolve("site.yml")

  private def makePlaybookPath(root: Path, playbook: Playbook): Path =
    root.resolve(playbook.getName + ".yml")

  private def makeRolePath(root: Path, role: Role): Path =
    root.resolve("roles" + FileSystems.getDefault.getSeparator + role.getName)

  private def saveVars(rootPath: Path, vars: Array[(String, util.Map[String, AnyRef])]): Unit = {
    rootPath.toFile.mkdirs()

    vars
      .groupBy({ p => p._1 })
      .map({ g =>
        val pairs = g._2
          .flatMap({ p => p._2.asScala.toSeq }).toMap
        val content = VarsRenderer.render(pairs)
        val path = rootPath.resolve(g._1)
        (path, content)
      })
      .foreach({ case (p, c) => Files.write(p, c.getBytes) })
  }
}
