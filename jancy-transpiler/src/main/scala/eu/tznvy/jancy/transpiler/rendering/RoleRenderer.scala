package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.Path

import scala.collection.JavaConverters._
import eu.tznvy.jancy.core.{Handler, Role, Task}
import eu.tznvy.jancy.transpiler.helpers.Filesystem


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
