package eu.tznvy.jancy.transpiler.discovery

/**
  * Represents a content file embedded in the configuration jar
  *
  * @param source           relative path in the jar
  * @param destination      relative desitination path in the filesystem
  */
case class ContentFile(
  source: String,
  destination: String)
