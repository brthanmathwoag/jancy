package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.{FileSystems, Files, Path}

import eu.tznvy.jancy.core.Role


object RoleRenderer {

  def render(role: Role, root: Path): Unit = {
    val tasksPath = makeTasksPath(root)
    tasksPath.toFile.getParentFile.mkdirs()
    Files.write(tasksPath, TaskRenderer.render(role.getTasks).getBytes)
  }

  private def makeTasksPath(root: Path): Path =
    root.resolve("tasks" + FileSystems.getDefault.getSeparator + "main.yml")
}
