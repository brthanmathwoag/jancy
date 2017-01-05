package eu.tznvy.jancy.transpiler.helpers

import java.io.InputStream
import java.nio.file.{Files, Path}

import scala.collection.JavaConverters._

class ConcreteFilesystem extends Filesystem {
  override def createDirectories(path: Path): Unit =
    Files.createDirectories(path)

  override def writeFile(path: Path, content: String): Unit =
    Files.write(path, content.getBytes)

  override def readFile(path: Path): String =
    Files.readAllLines(path).asScala.mkString("\n")

  override def testPath(path: Path): Boolean =
    Files.exists(path)

  override def copy(from: InputStream, to: Path): Unit =
    Files.copy(from, to)
}
