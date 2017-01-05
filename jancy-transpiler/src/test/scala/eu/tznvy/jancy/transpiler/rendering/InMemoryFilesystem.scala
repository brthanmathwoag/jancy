package eu.tznvy.jancy.transpiler.rendering

import java.io.InputStream
import java.nio.file.Path

import eu.tznvy.jancy.transpiler.helpers.Filesystem

import scala.collection.mutable


class InMemoryFilesystem extends Filesystem {
  private val files = mutable.Map[String, String]()

  override def createDirectories(path: Path): Unit =
    files += path.toFile.getName -> ""

  override def writeFile(path: Path, content: String): Unit =
    files += path.toFile.getName -> content

  override def readFile(path: Path): Option[String] =
    files.get(path.toFile.getName)

  override def testPath(path: Path): Boolean =
    files.contains(path.toFile.getName)

  override def copy(from: InputStream, to: Path): Unit = ???
}