package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core._
import eu.tznvy.jancy.modules.files.Copy
import eu.tznvy.jancy.modules.packaging.os.Apt
import eu.tznvy.jancy.modules.system.Service
import org.scalatest.FunSpec


class PlayRendererSpec extends FunSpec {

  describe("The PlayRenderer") {

    it("should render task name as first key") {

      val expected =
        """name: webservers
          |hosts:
          |- web01
          |- web-amazon-us
          |- web-amazon-eu
          |""".stripMargin

      val input = new Play("webservers")
        .hosts(
          new Host("web01"),
          new Group("web-amazon-us"),
          new Group("web-amazon-eu")
        )

      val actual = PlayRenderer.render(input)

      assertResult (expected) { actual }
    }

    it("should render keys alphabetically") {

      val expected =
        """name: webservers
          |hosts:
          |- web01
          |- web-amazon-us
          |- web-amazon-eu
          |roles:
          |- common
          |- site01
          |- site02
          |""".stripMargin

      val input = new Play("webservers")
        .hosts(
          new Host("web01"),
          new Group("web-amazon-us"),
          new Group("web-amazon-eu")
        ).roles(
        new Role("common"),
        new Role("site01"),
        new Role("site02")
      )

      val actual = PlayRenderer.render(input)

      assertResult (expected) { actual }
    }

    it("should omit keys with empty collections") {

      val expected =
        """name: webservers
          |roles:
          |- common
          |- site01
          |- site02
          |""".stripMargin

      val input = new Play("webservers")
        .roles(
          new Role("common"),
          new Role("site01"),
          new Role("site02"))
        .hosts()
        .tasks()
        .handlers()

      val actual = PlayRenderer.render(input)

      assertResult (expected) { actual }
    }

    it("should render tasks and handlers inline") {

      val expected =
        """name: webservers
          |handlers:
          |- name: Reload httpd
          |  service: |-
          |    name='apache2'
          |    state='reloaded'
          |hosts:
          |- web01
          |- web02
          |tasks:
          |- name: Install httpd
          |  apt: |-
          |    name='apache2'
          |    state='installed'
          |- name: Upload httpd config
          |  copy: |-
          |    dest='/etc/apache2'
          |    src='etc/apache2'
          |  notify: Reload httpd
          |""".stripMargin

      val input = new Play("webservers")
        .hosts(
          new Host("web01"),
          new Host("web02"))
        .tasks(
          new Apt()
            .name("apache2")
            .state("installed")
            .toTask("Install httpd"),
          new Copy()
            .src("etc/apache2")
            .dest("/etc/apache2")
            .toTask("Upload httpd config")
            .notify("Reload httpd"))
        .handlers(
          new Service()
            .name("apache2")
            .state("reloaded")
            .toHandler("Reload httpd"))

      val actual = PlayRenderer.render(input)

      assertResult (expected) { actual }
    }

    it("should render single-element hosts arrays as strings") {

      val expected =
        """name: webservers
          |hosts: web-01
          |""".stripMargin

      val input = new Play("webservers")
        .hosts(new Host("web-01"))

      val actual = PlayRenderer.render(input)

      assertResult (expected) { actual }
    }

    it("should render roles as a list regardless of its length") {

      val expected =
        """name: webservers
          |roles:
          |- web
          |""".stripMargin

      val input = new Play("webservers")
        .roles(new Role("web"))

      val actual = PlayRenderer.render(input)

      assertResult (expected) { actual }
    }

  }
}

