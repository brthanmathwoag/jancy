package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.{Host, Playbook, Group, Role}
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
        .hosts(
          new Host("web01"),
          new Group("web-amazon-us"),
          new Group("web-amazon-eu")
        ).roles(
          new Role("common"),
          new Role("site01"),
          new Role("site02")
        )

      val actual = PlaybookRenderer.render(input)

      assertResult (expected) { actual }
    }
  }
}

