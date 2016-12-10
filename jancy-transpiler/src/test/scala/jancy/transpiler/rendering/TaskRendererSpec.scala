package jancy.transpiler.rendering

import jancy.modules.files.Copy
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

      val task = new Copy("Upload httpd config")
        .src("etc/apache2/")
        .dest("/etc/apache2/")

      val actual = TaskRenderer.render(task)

      assertResult (expected) { actual }
    }
  }
}
