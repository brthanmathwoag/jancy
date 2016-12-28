package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.{Files, Path}
import scala.collection.JavaConverters._

import eu.tznvy.jancy.core.Role


object RoleRenderer {

  def render(role: Role, root: Path): Unit = {

    Seq(
      ("tasks", { () => TasklikeRenderer.render(role.getTasks) }),
      ("handlers", { () => TasklikeRenderer.render(role.getHandlers) }),
      ("vars", { () => VarsRenderer.render(role.getVars.asScala.toMap) }))
      .foreach({ case (k, v) => writeToFile(constructOutputFilePath(root, k), v()) }) //writeToFile(constructOutputFilePath(root, k), v()) })
  }

  private def constructOutputFilePath(root: Path, componentName: String) =
    root.resolve(componentName).resolve("main.yml")

  private def writeToFile(path: Path, content: String): Unit = {
    path.toFile.getParentFile.mkdirs
    Files.write(path, content.getBytes)
  }
}
