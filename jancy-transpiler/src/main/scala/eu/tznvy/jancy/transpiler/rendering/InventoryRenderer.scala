package eu.tznvy.jancy.transpiler.rendering

import eu.tznvy.jancy.core.{Host, Inventory, Group}
import eu.tznvy.jancy.transpiler.helpers.GroupsHelper

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

    val flattenedGroups = GroupsHelper.flattenSubgroups(inventory.getGroups)
    val standaloneHosts =
      inventory.getHosts.toSet -- flattenedGroups.flatMap(_.getHosts)

    (renderHosts(standaloneHosts) :: renderGroups(flattenedGroups).toList)
      .filter(_.length > 0)
      .mkString("\n\n")
  }

  override def renderAll(ts: Array[Inventory]) = ???
}
