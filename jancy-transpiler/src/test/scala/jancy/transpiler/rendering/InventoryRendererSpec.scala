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
          .groups(
            new Group("web")
              .hosts(
                new Host("w1"),
                new Host("w2")
              ),
            new Group("services")
              .hosts(
                new Host("s1"),
                new Host("s2"),
                new Host("s3")
              ),
            new Group("database")
              .hosts(
                new Host("d1")
              )
          )

      val actual = InventoryRenderer.render(inventory)

      assertResult (expected) { actual }
    }

    it ("should work for standalone hosts as well as groups") {
      val expected =
        """h1
          |h2
          |h3
          |h4
          |
          |[g1]
          |g1_h1
          |g1_h2
          |
          |[g2]
          |g2_h1""".stripMargin

      val inventory = new Inventory("inventory")
        .hosts(
          new Host("h1"),
          new Host("h2"),
          new Host("h3"),
          new Host("h4")
        )
        .groups(
          new Group("g1")
            .hosts(
              new Host("g1_h1"),
              new Host("g1_h2")
            ),
          new Group("g2")
            .hosts(
              new Host("g2_h1")
            )
        )

      val actual = InventoryRenderer.render(inventory)

      assertResult (expected) { actual }
    }
  }
}
