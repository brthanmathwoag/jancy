package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.Task
import eu.tznvy.jancy.modules.commands.Command
import eu.tznvy.jancy.modules.files.Copy
import eu.tznvy.jancy.modules.packaging.os.Apt
import eu.tznvy.jancy.modules.system.{Ufw, User}
import org.scalatest.FunSpec

import scala.beans.BeanProperty
import scala.collection.JavaConverters._

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
        """name: Install httpd
          |apt: |-
          |  name='apache2'
          |  state='present'
          |delay: '10'
          |register: result
          |when: ansible_os_family == "Debian"
          |""".stripMargin

      val task = new Task("Install httpd")
          .action(new Apt()
            .state("present")
            .name("apache2"))
        .delay("10")
        .register("result")
        .when("ansible_os_family == \"Debian\"")

      val actual = TasklikeRenderer.render(task)

      assertResult (expected) { actual }
    }

    it("should render arrays of multiple elements as lists") {

      val expected =
        """name: Install some pre-requisites
          |apt: |-
          |  name='{{ item }}'
          |  state='present'
          |with_items:
          |- apache2
          |- subversion
          |- libapache2-mod-svn
          |- apache2-utils
          |- anacron
          |""".stripMargin

      val task = new Task("Install some pre-requisites")
        .action(new Apt()
          .state("present")
          .name("{{ item }}"))
        .withItems("apache2", "subversion", "libapache2-mod-svn", "apache2-utils", "anacron")

      val actual = TasklikeRenderer.render(task)

      assertResult (expected) { actual }
    }

    it("should render arrays of a single element as a string") {

      val expected =
        """name: Install some pre-requisites
          |apt: |-
          |  name='{{ item }}'
          |  state='present'
          |with_items: apache2
          |""".stripMargin

      val task = new Task("Install some pre-requisites")
        .action(new Apt()
          .state("present")
          .name("{{ item }}"))
        .withItems("apache2")

      val actual = TasklikeRenderer.render(task)

      assertResult (expected) { actual }
    }

    it("should render arrays of a maps as json objects") {

      val expected =
        """name: Add several users
          |user: |-
          |  groups='{{ item.groups }}'
          |  name='{{ item.name }}'
          |  state='present'
          |with_items:
          |- name: testuser1
          |  groups: wheel
          |- name: testuser2
          |  groups: root
          |""".stripMargin

      val task = new Task("Add several users")
        .action(new User()
          .name("{{ item.name }}")
          .state("present")
          .groups("{{ item.groups }}"))
        .withItems(
          Map("name" -> "testuser1", "groups" -> "wheel").asJava,
          Map("name" -> "testuser2", "groups" -> "root").asJava
        )

      val actual = TasklikeRenderer.render(task)

      assertResult (expected) { actual }
    }

    it("should render free-form argument without the key, as the first argument") {

      val expected =
        """name: Test to see if selinux is running
          |command: |-
          |  getenforce
          |  chdir='/'
          |register: sestatus
          |""".stripMargin

      val task = new Task("Test to see if selinux is running")
        .action(new Command()
          .freeForm("getenforce")
          .chdir("/"))
        .register("sestatus")

      val actual = TasklikeRenderer.render(task)

      assertResult (expected) { actual }
    }
  }
}
