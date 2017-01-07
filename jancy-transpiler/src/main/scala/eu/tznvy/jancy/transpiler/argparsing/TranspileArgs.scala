package eu.tznvy.jancy.transpiler.argparsing

import java.io.File

case class TranspileArgs (
  jar: File,
  output: File
) extends Args
