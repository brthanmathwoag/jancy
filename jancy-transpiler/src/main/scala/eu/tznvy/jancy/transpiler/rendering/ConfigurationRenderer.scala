package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.{FileSystems, Files, Path}

import eu.tznvy.jancy.core.{Configuration, Inventory, Playbook, Role}

object ConfigurationRenderer {

  def render(configuration: Configuration, root: Path): Unit = {
    root.toFile.mkdirs()

    configuration
      .getInventories
      .map({ i => (makeInventoryPath(root, i), InventoryRenderer.render(i)) })
      .foreach({ case (p, c) => Files.write(p, c.getBytes) })

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
}
