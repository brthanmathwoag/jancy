package eu.tznvy.jancy.modulesgen.helpers

import scala.annotation.tailrec
import java.io.File

/**
  * A wrapper for IO operations
  */
object Filesystem {

  def getFilesRecursively(path: String): Seq[File] = {
    @tailrec
    def loop(toVisit: List[File], resultSoFar: List[File]): List[File] =
      toVisit match {
        case Nil => resultSoFar
        case f :: fs if f.isDirectory => loop(f.listFiles.toList ::: fs, resultSoFar)
        case f :: fs if !f.isDirectory => loop(fs, f :: resultSoFar)
      }

    loop(List(new File(path)), List())
  }
}
