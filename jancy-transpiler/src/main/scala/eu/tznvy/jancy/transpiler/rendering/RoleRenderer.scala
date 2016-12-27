package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.{Files, Path}

import eu.tznvy.jancy.core.Role


object RoleRenderer {

  def render(role: Role, root: Path): Unit = {
    writeToFile(
      constructOutputFilePath(root, "tasks"),
      TasklikeRenderer.render(role.getTasks))

    writeToFile(
      constructOutputFilePath(root, "handlers"),
      TasklikeRenderer.render(role.getHandlers))
  }

  private def constructOutputFilePath(root: Path, componentName: String) =
    root.resolve(componentName).resolve("main.yml")

  private def writeToFile(path: Path, content: String): Unit = {
    path.toFile.getParentFile.mkdirs
    Files.write(path, content.getBytes)
  }
}
