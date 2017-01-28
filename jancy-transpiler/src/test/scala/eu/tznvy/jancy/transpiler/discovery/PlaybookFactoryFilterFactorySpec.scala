package eu.tznvy.jancy.transpiler.discovery

import eu.tznvy.jancy.core.{Playbook, PlaybookFactory}
import org.scalatest.FunSpec
import scala.collection.JavaConverters._

class PlaybookFactoryFilterFactorySpec extends FunSpec {

  class C1
  class C2 extends PlaybookFactory {
    override def build = new Playbook("p1")
  }
  class C3
  class C4
  class C5 extends PlaybookFactory {
    override def build = new Playbook("p2")
  }
  val classes = List(classOf[C1], classOf[C2], classOf[C3], classOf[C4], classOf[C5])

  describe("The PlaybookFactoryFilterFactory") {

    describe("when no classname is provided") {

      val filter = PlaybookFactoryFilterFactory(None)

      it("should pick all PlaybookFactory implementations") {

        val expected = List(classOf[C2], classOf[C5])
        val actual = classes.filter(filter)
        assertResult(expected) { actual }
      }
    }

    describe("when a classname is provided") {

      describe("and this class implements PlaybookFactory") {

        val filter = PlaybookFactoryFilterFactory(Some(classOf[C2].getName))

        it("should only pick this class") {

          val expected = List(classOf[C2])
          val actual = classes.filter(filter)
          assertResult(expected) { actual }
        }
      }

      describe("otherwise") {

        val filter = PlaybookFactoryFilterFactory(Some(classOf[C1].getName))

        it("it should return nothing") {

          val expected = List[Class[_]]()
          val actual = classes.filter(filter)
          assertResult(expected) { actual }
        }
      }

      //this might actually be a nice feature
      it("should match exact, fully qualified names only") {

        val filter = PlaybookFactoryFilterFactory(Some(classOf[C2].getSimpleName))
        val expected = List[Class[_]]()
        val actual = classes.filter(filter)
        assertResult(expected) { actual }
      }
    }
  }
}
