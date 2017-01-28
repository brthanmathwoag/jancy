package eu.tznvy.jancy.transpiler.discovery

import eu.tznvy.jancy.core.PlaybookFactory

/**
  * Creates a filter to pick PlaybookFactory implementations
  */
object PlaybookFactoryFilterFactory {
    def apply(maybeName: Option[String]): Class[_] => Boolean = {

      def implementsPlaybookFactory(c: Class[_]): Boolean =
        c.getInterfaces.exists(_.getName == classOf[PlaybookFactory].getName)

      def alwaysTrue(c: Class[_]): Boolean = true

      val nameFilter =
        maybeName
          .map({ n =>
            { (c: Class[_]) => n == c.getName }
          })
          .getOrElse(alwaysTrue _)

      { (c: Class[_]) => nameFilter(c) && implementsPlaybookFactory(c) }
    }
}
