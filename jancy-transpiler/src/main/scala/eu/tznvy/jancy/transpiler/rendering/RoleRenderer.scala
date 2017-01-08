package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.Path

import scala.collection.JavaConverters._
import eu.tznvy.jancy.core.Role
import eu.tznvy.jancy.transpiler.helpers.Filesystem

/**
  * Creates YAML representation for Roles on a provided filesystem
  *
  * @param filesystem     a wrapper for IO operations
  */
class RoleRenderer(filesystem: Filesystem) {

  def render(role: Role, root: Path): Unit = {

    case class Rendering[T](directoryName: String,  items: Array[T], renderer: Renderer[T]) {
      def apply(): Unit = {
        val outputPath = root
          .resolve(role.getName)
          .resolve(directoryName)
          .resolve("main.yml")

        if (!items.isEmpty) {
          filesystem.writeFile(outputPath, renderer.renderAll(items))
        }
      }
    }

    Seq[Rendering[_]](
      Rendering("tasks", role.getTasks, new TaskRenderer()),
      Rendering("handlers", role.getHandlers, new HandlerRenderer()),
      Rendering("vars", role.getVars.asScala.toArray[(String, Any)], VarsRenderer)
    ).foreach(_())
  }
}
