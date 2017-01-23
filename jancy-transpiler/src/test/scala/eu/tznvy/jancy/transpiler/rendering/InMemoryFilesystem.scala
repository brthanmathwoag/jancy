package eu.tznvy.jancy.transpiler.rendering

import java.io.InputStream
import java.nio.file.Path
import scala.annotation.tailrec
import eu.tznvy.jancy.transpiler.helpers.Filesystem

import scala.collection.mutable

/**
  * A mock Filesystem
  */
class InMemoryFilesystem extends Filesystem {
  private val files = mutable.Map[String, String]()

  override def createDirectories(path: Path): Unit =
    files ++= getAncestors(path).map(_.toFile.getPath -> "")

  override def writeFile(path: Path, content: String): Unit = {
    files ++= getAncestors(path).map(_.toFile.getPath -> "")
    files += path.toFile.getPath -> content
  }

  override def readFile(path: Path): Option[String] =
    files.get(path.toFile.getPath)

  override def testPath(path: Path): Boolean =
    files.contains(path.toFile.getPath)

  override def copy(from: InputStream, to: Path): Unit = ???

  private def getAncestors(path: Path) = {

    @tailrec
    def loop(current: Path, ancestors: List[Path]): List[Path] =
      if (current == null) ancestors
      else loop(current.getParent, current :: ancestors)

    loop(path, List())
  }
}
