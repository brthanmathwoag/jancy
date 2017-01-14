package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.{Host, Inventory, Group}
import scala.annotation.tailrec

/**
  * Creates an INI representation for Inventories
  */
object InventoryRenderer extends Renderer[Inventory] {
  def render(inventory: Inventory): String = {
  
    def renderHosts(hosts: Iterable[Host]): String =
      hosts.map(_.getName).mkString("\n")

    def renderSubgroups(group: Group): String = {
      val subgroups = group.getSubgroups.map(_.getName).mkString("\n")
      if (subgroups.length > 0) s"[${group.getName}:children]\n$subgroups"
      else ""
    }

    def renderGroups(groups: Seq[Group]): Seq[String] = {
      groups.flatMap({ g =>
        val hosts = renderHosts(g.getHosts)
        val mainSection = s"[${g.getName}]\n$hosts"
        val subgroupSection = renderSubgroups(g)

        Seq(mainSection, subgroupSection).filter(_.length > 0)
      })
    }

    def flattenSubgroups(groups: Seq[Group]): Seq[Group] = {
      @tailrec
      def loop(toVisit: List[Group],
          namesSoFar: Set[String],
          resultsSoFar: List[Group]) : List[Group] =
        toVisit match {
          case Nil => resultsSoFar
          case g :: gs =>
            if (namesSoFar.contains(g.getName))
              throw new Error("Cyclic reference detected")
            else loop(
              gs ++ g.getSubgroups,
              namesSoFar + g.getName,
              g :: resultsSoFar)
        }
      loop(groups.toList, Set(), List()).reverse
    }

    val flattenedGroups = flattenSubgroups(inventory.getGroups)
    val standaloneHosts =
      inventory.getHosts.toSet -- flattenedGroups.flatMap(_.getHosts)

    (renderHosts(standaloneHosts) :: renderGroups(flattenedGroups).toList)
      .filter(_.length > 0)
      .mkString("\n\n")
  }

  override def renderAll(ts: Array[Inventory]) = ???
}
