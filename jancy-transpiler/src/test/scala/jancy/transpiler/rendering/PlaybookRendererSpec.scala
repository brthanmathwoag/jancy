package jancy.transpiler.rendering

import jancy.core.{Host, Playbook, Group, Role}
import org.scalatest.FunSpec


class PlaybookRendererSpec extends FunSpec {

  describe("The PlaybookRenderer") {

    it("should work for a simple playbook") {

      val expected =
        """hosts:
          |- web01
          |- web-amazon-us
          |- web-amazon-eu
          |name: webservers
          |roles:
          |- common
          |- site01
          |- site02
          |""".stripMargin

      val input = new Playbook("webservers")
        .hosts(Array(
          new Host("web01"),
          new Group("web-amazon-us"),
          new Group("web-amazon-eu")
        )).roles(Array(
          new Role("common"),
          new Role("site01"),
          new Role("site02")
        ))

      val actual = PlaybookRenderer.render(input)

      assertResult (expected) { actual }
    }
  }
}

