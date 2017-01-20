package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.Paths

import scala.collection.JavaConverters._
import org.scalatest.FunSpec
import eu.tznvy.jancy.core._

class PlaybookRendererSpec extends FunSpec {
  describe("The PlaybookRenderer") {
    it("should render inventories to files named after the inventory") {

      val playbook = new Playbook("c1")
        .inventories(
          new Inventory("production").hosts(new Host("h1")),
          new Inventory("stage").hosts(new Host("h2")),
          new Inventory("dev").hosts(new Host("h3")))

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      Seq("production", "stage", "dev")
        .map(Paths.get("/", "c1", _))
        .foreach({ p =>
          assert(filesystem.testPath(p))
          assert(filesystem.readFile(p).map(_.length).getOrElse(0) > 0)
        })
    }

    it("should render plays in /site.yml file") {

      val playbook = new Playbook("c1")
        .plays(
          new Play("p1"),
          new Play("p2"),
          new Play("p3")
        )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      val expectedPath = Paths.get("/c1/site.yml")
      assert(filesystem.testPath(expectedPath))
      assert(filesystem.readFile(expectedPath).map(_.length).getOrElse(0) > 0)
    }

    it("should render host vars in /host_vars/$host_name") {

      val playbook = new Playbook("c1")
          .inventories(
            new Inventory("inventory")
              .hosts(
                new Host("h1")
                  .vars(Map[String, AnyRef]("foo" -> "bar").asJava)
          )
        )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      val expectedPath = Paths.get("/c1/host_vars/h1")
      assert(filesystem.testPath(expectedPath))
      assert(filesystem.readFile(expectedPath).map(_.length).getOrElse(0) > 0)
    }

    it("should not render host vars if a host does not have any") {

      val playbook = new Playbook("c1")
        .inventories(
          new Inventory("inventory")
            .hosts(
              new Host("h1")
                .vars(Map[String, AnyRef]("foo" -> "bar").asJava),
              new Host("h2")
            )
        )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      val expectedPath = Paths.get("/c1/host_vars/h2")
      assert(!filesystem.testPath(expectedPath))
    }

    it("should not create host_vars directory if there are none") {

      val playbook = new Playbook("c1")
        .inventories(
          new Inventory("inventory")
            .hosts(
              new Host("h1"),
              new Host("h2")
            )
        )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      val expectedPath = Paths.get("/c1/host_vars")
      assert(!filesystem.testPath(expectedPath))
    }

    it("should render group vars in /group_vars/$group_name") {

      val playbook = new Playbook("c1")
        .inventories(
          new Inventory("inventory")
            .groups(
              new Group("g1")
                .vars(Map[String, AnyRef]("foo" -> "bar").asJava)
            )
        )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      val expectedPath = Paths.get("/c1/group_vars/g1")
      assert(filesystem.testPath(expectedPath))
      assert(filesystem.readFile(expectedPath).map(_.length).getOrElse(0) > 0)
    }

    it("should not render group vars if a group does not have any") {

      val playbook = new Playbook("c1")
        .inventories(
          new Inventory("inventory")
            .groups(
              new Group("g1")
                .vars(Map[String, AnyRef]("foo" -> "bar").asJava),
              new Group("g2")
            )
        )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      val expectedPath = Paths.get("/c1/group_vars/g2")
      assert(!filesystem.testPath(expectedPath))
    }

    it("should not create group_vars directory if there are none") {

      val playbook = new Playbook("c1")
        .inventories(
          new Inventory("inventory")
            .groups(
              new Group("g1"),
              new Group("g2")
            )
        )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      val expectedPath = Paths.get("/c1/group_vars")
      assert(!filesystem.testPath(expectedPath))
    }

    it("should render inventory vars in /group_vars/all") {

      val playbook = new Playbook("c1")
        .inventories(
          new Inventory("inventory")
            .vars(Map[String, AnyRef]("foo" -> "bar").asJava)
        )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      val expectedPath = Paths.get("/c1/group_vars/all")
      assert(filesystem.testPath(expectedPath))
    }
  }
}
