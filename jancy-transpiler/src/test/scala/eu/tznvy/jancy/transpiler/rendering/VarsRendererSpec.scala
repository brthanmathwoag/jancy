package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.Host
import org.scalatest.FunSpec
import scala.collection.JavaConverters._

class VarsRendererSpec extends FunSpec {

  describe("The VarsRenderer") {

    it("should render vars alphabetically") {

      val expected =
        """apacheMaxClients: '900'
          |apacheMaxRequestsPerChild: '3000'
          |backup: backup-atlanta.example.com
          |ntp: ntp-atlanta.example.com
          |""".stripMargin

      val vars = new Host("h1")
        .vars(
          Map(
            "apacheMaxRequestsPerChild" -> "3000",
            "apacheMaxClients" -> "900",
            "ntp" -> "ntp-atlanta.example.com",
            "backup" -> "backup-atlanta.example.com")
            .asJava
            .asInstanceOf[java.util.Map[String, AnyRef]])
        .getVars
        .asScala
        .toMap

      val actual = VarsRenderer.render(vars)

      assertResult(expected) { actual }
    }
  }
}