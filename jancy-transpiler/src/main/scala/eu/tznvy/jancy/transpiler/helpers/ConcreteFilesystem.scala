package eu.tznvy.jancy.transpiler.helpers

import java.io.InputStream
import java.nio.file.{Files, Path}

import scala.collection.JavaConverters._
import scala.util.Try

class ConcreteFilesystem extends Filesystem {
  override def createDirectories(path: Path): Unit =
    Files.createDirectories(path)

  override def writeFile(path: Path, content: String): Unit = {
    Files.createDirectories(path.getParent)
    Files.write(path, content.getBytes)
  }


  override def readFile(path: Path): Option[String] =
    Try { Files.readAllLines(path) }
      .map(_.asScala.mkString("\n"))
      .toOption

  override def testPath(path: Path): Boolean =
    Files.exists(path)

  override def copy(from: InputStream, to: Path): Unit =
    Files.copy(from, to)
}
