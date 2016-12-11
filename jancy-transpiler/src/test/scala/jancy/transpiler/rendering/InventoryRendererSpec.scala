package jancy.transpiler.rendering

import jancy.core.{Inventory, Host, Group}
import org.scalatest.FunSpec

class InventoryRendererSpec extends FunSpec {

  describe("The InventoryRenderer") {

    it("should work for a simple inventory") {

      val expected =
        """[web]
          |w1
          |w2
          |
          |[services]
          |s1
          |s2
          |s3
          |
          |[database]
          |d1""".stripMargin

      val inventory = new Inventory("production")
          .groups(Array(
            new Group("web")
              .hosts(Array(
                new Host("w1"),
                new Host("w2")
              )),
            new Group("services")
              .hosts(Array(
                new Host("s1"),
                new Host("s2"),
                new Host("s3")
              )),
            new Group("database")
              .hosts(Array(
                new Host("d1")
              ))
          ))

      val actual = InventoryRenderer.render(inventory)

      assertResult (expected) { actual }
    }
  }
}
