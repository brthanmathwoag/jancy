package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.Paths

import eu.tznvy.jancy.core.Role
import eu.tznvy.jancy.modules.files.Copy
import org.scalatest.FunSpec
import scala.collection.JavaConverters._

class RoleRendererSpec extends FunSpec {

  describe("The RoleRenderer") {

    it("should render tasks in roles/$NAME/tasks/main.yml") {
      val role = new Role("webserver")
        .tasks(
          new Copy()
            .src("etc/apache2/")
            .dest("/etc/apache2/")
            .toTask("Upload httpd config"))
      val rootPath = Paths.get("/test/root/path")
      val filesystem = new InMemoryFilesystem

      new RoleRenderer(filesystem).render(role, rootPath)

      val expectedPath = Paths.get("/test/root/path/roles/webserver/tasks/main.yml")
      val contentLength = filesystem.readFile(expectedPath).map(_.length)

      assert(contentLength.isDefined && contentLength.get > 0)
    }

    it("should render handlers in roles/$NAME/handlers/main.yml") {
      val role = new Role("webserver")
        .handlers(
          new Copy()
            .src("etc/apache2/")
            .dest("/etc/apache2/")
            .toHandler("Upload httpd config"))
      val rootPath = Paths.get("/test/root/path")
      val filesystem = new InMemoryFilesystem

      new RoleRenderer(filesystem).render(role, rootPath)

      val expectedPath = Paths.get("/test/root/path/roles/webserver/handlers/main.yml")
      val contentLength = filesystem.readFile(expectedPath).map(_.length)

      assert(contentLength.isDefined && contentLength.get > 0)
    }

    it("should not create a file for handlers if there are none") {
      val role = new Role("webserver")
        .tasks(
          new Copy()
            .src("etc/apache2/")
            .dest("/etc/apache2/")
            .toTask("Upload httpd config"))
      val rootPath = Paths.get("/test/root/path")
      val filesystem = new InMemoryFilesystem

      new RoleRenderer(filesystem).render(role, rootPath)

      val expectedPath = Paths.get("/test/root/path/roles/webserver/handlers/main.yml")

      assert(!filesystem.testPath(expectedPath))
    }

    it("should render vars in vars/$NAME/vars/main.yml") {
      val role = new Role("webserver")
          .vars(
            Map[String, AnyRef](
              "foo" -> "bar",
              "phasers" -> "stun",
              "answer" -> 42.asInstanceOf[Integer]
            ).asJava)

      val rootPath = Paths.get("/test/root/path")
      val filesystem = new InMemoryFilesystem

      new RoleRenderer(filesystem).render(role, rootPath)

      val expectedPath = Paths.get("/test/root/path/roles/webserver/vars/main.yml")
      val contentLength = filesystem.readFile(expectedPath).map(_.length)

      assert(contentLength.isDefined && contentLength.get > 0)
    }

    it("should not create a file for vars if there are none") {
      val role = new Role("webserver")
        .tasks(
          new Copy()
            .src("etc/apache2/")
            .dest("/etc/apache2/")
            .toTask("Upload httpd config"))
      val rootPath = Paths.get("/test/root/path")
      val filesystem = new InMemoryFilesystem

      new RoleRenderer(filesystem).render(role, rootPath)

      val expectedPath = Paths.get("/test/root/path/roles/webserver/handlers/main.yml")

      assert(!filesystem.testPath(expectedPath))
    }
  }
}

