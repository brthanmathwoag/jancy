package eu.tznvy.jancy.transpiler.discovery

import eu.tznvy.jancy.core.PlaybookFactory

/**
  * Finds and instantiates classes implementing the PlaybookFactory interface
  * in a specified class source.
  */
object PlaybookFactoriesDiscoverer {

  def findPlaybookFactories(
    source: JarClassSource,
    classname: Option[String]
  ): Seq[PlaybookFactory] =
    source
      .iterate
      .filter(PlaybookFactoryFilterFactory(classname))
      .map(_
        .newInstance
        .asInstanceOf[PlaybookFactory])
      .toList
}
