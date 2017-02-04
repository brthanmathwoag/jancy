package eu.tznvy.jancy.transpiler.helpers

import eu.tznvy.jancy.core.Group

import scala.annotation.tailrec

object GroupsHelper {

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
}
