package eu.tznvy.jancy.transpiler.helpers

import java.io.InputStream
import java.nio.file.Path

trait Filesystem {
  def createDirectories(path: Path): Unit

  def writeFile(path: Path, content: String): Unit

  def readFile(path: Path): String

  def testPath(path: Path): Boolean

  def copy(from: InputStream, to: Path): Unit
}
