package eu.tznvy.jancy.transpiler.rendering

import java.nio.file.Paths

import scala.collection.JavaConverters._
import org.scalatest.FunSpec
import eu.tznvy.jancy.core._
import eu.tznvy.jancy.modules.system.Ping

class PlaybookRendererSpec extends FunSpec {
  describe("The PlaybookRenderer") {
    it ("should render a single inventory to a file named after the inventory") {
      val playbook = new Playbook("c1")
        .inventories(new Inventory("inventory").hosts(new Host("h1")))

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      val expected = Paths.get("/", "c1", "inventory")
      assert(filesystem.testPath(expected))
      assert(filesystem.readFile(expected).map(_.length).getOrElse(0) > 0)
    }

    it ("should render multiple inventories to a 'hosts' file in a directory named after the inventory") {
      val playbook = new Playbook("c1")
        .inventories(
          new Inventory("production").hosts(new Host("h1")),
          new Inventory("stage").hosts(new Host("h2")),
          new Inventory("dev").hosts(new Host("h3")))

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      Seq("production", "stage", "dev")
        .map(Paths.get("/", "c1", "inventories", _, "hosts"))
        .foreach({ p =>
          assert(filesystem.testPath(p))
          assert(filesystem.readFile(p).map(_.length).getOrElse(0) > 0)
        })
    }

    it ("should allow adding a group to several inventories") {
      val group = new Group("g1")
      val inventory1 = new Inventory("i1").groups(group)
      val inventory2 = new Inventory("i2").groups(group)
      val playbook = new Playbook("c1").inventories(inventory1, inventory2)

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))
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

    it("should render roles to directories named after the role") {

      val playbook = new Playbook("c1")
        .roles(
          new Role("common")
            .tasks(
              new Task("do this").action(new Ping)
            ),
          new Role("backend")
            .tasks(
              new Task("do that").action(new Ping)
            ),
          new Role("frontend")
            .tasks(
              new Task("do something else").action(new Ping)
            )
      )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/c1"))

      Seq("common", "backend", "frontend")
        .map(Paths.get("/", "c1", "roles", _))
        .foreach({ p =>
          assert(filesystem.testPath(p))
        })
    }

    it("should render host vars even if the host is mentioned only via a group") {

      val playbook = new Playbook("site")
          .inventories(
            new Inventory("inventory")
                .groups(
                  new Group("g1")
                      .hosts(
                        new Host("h1")
                            .vars(Map[String, AnyRef]("foo" -> "bar").asJava)
                      )
                )
          )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/site"))

      val expectedPath = Paths.get("/site/host_vars/h1")
      assert(filesystem.testPath(expectedPath))
      assert(filesystem.readFile(expectedPath).map(_.length).getOrElse(0) > 0)
    }

    it("should render vars for subgroups") {

      val playbook = new Playbook("site")
        .inventories(
          new Inventory("inventory")
            .groups(
              new Group("g1")
                  .subgroups(
                  new Group("g2")
                    .vars(Map[String, AnyRef]("foo" -> "bar").asJava)
                )
            )
        )

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/site"))

      val expectedPath = Paths.get("/site/group_vars/g2")
      assert(filesystem.testPath(expectedPath))
      assert(filesystem.readFile(expectedPath).map(_.length).getOrElse(0) > 0)
    }

    it ("should keep group vars for each inventory separately") {

      val g1 = new Group("g1")
        .vars(Map[String, AnyRef](
          "vg1" -> "foo",
          "vg2" -> "bar"
        ).asJava)

      val i1 = new Inventory("i1")
        .groups(
          g1
            .vars(Map[String, AnyRef](
              "vg2" -> "bar1",
              "vg3" -> "baz1"
            ).asJava))

      val i2 = new Inventory("i2")
        .groups(
          g1
            .vars(Map[String, AnyRef](
              "vg2" -> "bar2",
              "vg4" -> "baz2"
            ).asJava))

      val playbook = new Playbook("p1")
        .inventories(i1, i2)

      val filesystem = new InMemoryFilesystem
      val playbookRenderer = new PlaybookRenderer(filesystem)
      playbookRenderer.render(playbook, Paths.get("/p1"))

      assert(filesystem.readFile(Paths.get("/p1/inventories/i1/group_vars/g1")) == Some("vg2: bar1\nvg3: baz1\n"))
      assert(filesystem.readFile(Paths.get("/p1/inventories/i2/group_vars/g1")) ==  Some("vg2: bar2\nvg4: baz2\n"))
    }
  }
}
