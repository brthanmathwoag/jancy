package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.Task
import eu.tznvy.jancy.modules.files.Copy
import eu.tznvy.jancy.modules.packaging.os.Apt
import eu.tznvy.jancy.modules.system.Ufw
import org.scalatest.FunSpec

class TaskRendererSpec extends FunSpec {

  describe("The TaskRenderer") {

    it("should render task name as first key") {

      val expected =
        """name: Upload httpd config
          |copy: |-
          |  dest='/etc/apache2/'
          |  src='etc/apache2/'
          |""".stripMargin

      val task = new Copy()
        .src("etc/apache2/")
        .dest("/etc/apache2/")
        .toTask("Upload httpd config")

      val actual = TasklikeRenderer.render(task)

      assertResult (expected) { actual }
    }

    it("should render action arguments alphabetically") {

      val expected =
        """name: Allow ssh through firewall
          |ufw: |-
          |  direction='in'
          |  port='22'
          |  proto='tcp'
          |  rule='allow'
          |""".stripMargin

      val task = new Ufw()
        .rule("allow")
        .direction("in")
        .proto("tcp")
        .port("22")
        .toTask("Allow ssh through firewall")

      val actual = TasklikeRenderer.render(task)

      assertResult (expected) { actual }
    }

    it("should render action as second key, followed by other keys ordered alphabetically") {

      val expected =
        """name: Install some pre-requisites
          |apt: |-
          |  name='{{ item }}'
          |  state='present'
          |when: ansible_os_family == "Debian"
          |with_items: apache2, subversion, libapache2-mod-svn, apache2-utils, anacron
          |""".stripMargin

      val task = new Task("Install some pre-requisites")
          .action(new Apt()
            .state("present")
            .name("{{ item }}"))
        .withItems("apache2, subversion, libapache2-mod-svn, apache2-utils, anacron")
        .when("ansible_os_family == \"Debian\"")

      val actual = TasklikeRenderer.render(task)

      assertResult (expected) { actual }
    }
  }
}
