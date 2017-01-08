package eu.tznvy.jancy.transpiler.argparsing

import java.io.File

/**
  * Represents the user intent to convert Configurations in a jar into
  * YAML in a specified directory
  *
  * @param jar        the jar with ConfigurationFactory implementations
  * @param output     the target directory. Current working directory if not
  *                   specified.
  */
case class TranspileArgs (
  jar: File,
  output: File
) extends Args
