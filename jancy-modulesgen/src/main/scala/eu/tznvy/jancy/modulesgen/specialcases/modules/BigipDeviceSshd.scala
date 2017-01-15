package eu.tznvy.jancy.modulesgen.specialcases.modules

import eu.tznvy.jancy.modulesgen.specialcases.SpecialCase

object BigipDeviceSshd extends SpecialCase {

  override def moduleName: String = "bigip_device_sshd"

  override def optionsWithNoEnum: Set[String] = Set("allow")
}
