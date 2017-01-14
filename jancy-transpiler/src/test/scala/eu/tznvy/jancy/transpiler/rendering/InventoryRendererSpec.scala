package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.{Inventory, Host, Group}
import org.scalatest.FunSpec
import scala.concurrent.{Future, Promise}
import scala.util.Try
import scala.concurrent.ExecutionContext.Implicits.global

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

    it("should render subgroups under $group_name:children section") {

      val expected =
        """[g1]
          |g1_h1
          |
          |[g2]
          |g2_h1
          |
          |[g2:children]
          |g2_1
          |
          |[g3]
          |g3_h1
          |g3_h2
          |
          |[g2_1]
          |g2_1_h1
          |g2_1_h2""".stripMargin

      val g1 = new Group("g1")
        .hosts(new Host("g1_h1"))

      val g2 = new Group("g2")
        .hosts(new Host("g2_h1"))
        .subgroups(
            new Group("g2_1")
                .hosts(new Host("g2_1_h1"), new Host("g2_1_h2")))

      val g3 = new Group("g3")
        .hosts(new Host("g3_h1"), new Host("g3_h2"))

      val inventory = new Inventory("inventory")
        .groups(g1, g2, g3)

      val actual = InventoryRenderer.render(inventory)

      assertResult (expected) { actual }
    }

    it("should throw an exception if groups' object graph is cyclic") {

      class MutableGroup(name: String) extends Group(name) {
        var subgroup: Group = null
        override def getSubgroups(): Array[Group] = Array(subgroup)
      }

      val g1 = new MutableGroup("g1")
      val g2 = new MutableGroup("g2")
      g1.subgroup = g2
      g2.subgroup = g1

      val inventory = new Inventory("inventory").groups(g1, g2)

      val (cancellationToken, future) = cancellable(Future {
        InventoryRenderer.render(inventory)
      })

      Thread.sleep(1000)
      if (!future.isCompleted) {
        cancellationToken()
        fail("Possible infite recursion detected")
      } else assert((future.value).get.isFailure)
    }

    it("should throw an exception if groups' by-name references graph is cyclic") {

      val inventory = new Inventory("inventory")
        .groups(
          new Group("g1").subgroups(new Group("g2")),
          new Group("g2").subgroups(new Group("g1")))
          
      assertThrows[Error] {
        InventoryRenderer.render(inventory)
      }
    }
  }

  def cancellable[T](f: Future[T]): (() => Unit, Future[T]) = {
    val p = Promise[T]
    val first = Future.firstCompletedOf(Seq(p.future, f))
    val cancellation: () => Unit =
      () => p.failure(new Exception)
    (cancellation, first)
  }
}
