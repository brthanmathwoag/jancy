package eu.tznvy.jancy.transpiler.argparsing

import java.io.File

/**
  * Represents the user intent to convert Configurations in a jar into
  * YAML in a specified directory
  *
  * @param jar        the jar with PlaybookFactory implementations
  * @param output     the target directory. Current working directory if not
  *                   specified.
  * @param classname  the name of a particular PlaybookFactory the user wants to use
  */
case class TranspileArgs (
  jar: File,
  output: File,
  classname: Option[String]
) extends Args
