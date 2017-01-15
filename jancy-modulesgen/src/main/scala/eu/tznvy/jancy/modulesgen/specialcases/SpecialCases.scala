package eu.tznvy.jancy.modulesgen.specialcases

import eu.tznvy.jancy.modulesgen.specialcases.modules._

object SpecialCases {

  private val allObjects: Map[String, SpecialCase] =
    Seq(Blockinfile, Lineinfile, BigipDeviceSshd, IpmiBoot, IpmiPower,
      BigipVirtualServer, PostgresqlUser)
      .map({ o => (o.moduleName, o) })
      .toMap

  def apply(name: String): SpecialCase = allObjects(name)

  def isDefinedAt(name: String): Boolean = allObjects.isDefinedAt(name)

  def get(name: String): Option[SpecialCase] = allObjects.get(name)
}
