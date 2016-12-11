package jancy.transpiler.rendering

import jancy.core.Task
import jancy.modules.files.Copy
import jancy.modules.packaging.os.Apt
import org.scalatest.FunSpec

class TaskRendererSpec extends FunSpec {

  describe("The TaskRenderer") {

    it("should work for a simple task") {

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

      val actual = TaskRenderer.render(task)

      assertResult (expected) { actual }
    }

    it("should work with task arguments") {

      val expected =
        """apt: |-
          |  name='{{ item }}'
          |  state='present'
          |name: Install some pre-requisites
          |with_items: apache2, subversion, libapache2-mod-svn, apache2-utils, anacron
          |when: ansible_os_family == "Debian"
          |""".stripMargin

      val task = new Task("Install some pre-requisites")
          .action(new Apt()
            .state("present")
            .name("{{ item }}"))
        .withItems("apache2, subversion, libapache2-mod-svn, apache2-utils, anacron")
        .when("ansible_os_family == \"Debian\"")

      val actual = TaskRenderer.render(task)

      assertResult (expected) { actual }
    }
  }
}
