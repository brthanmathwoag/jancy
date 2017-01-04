import sbt._
import sbt.Keys._

object Helpers {
  def getFilesRecursively(f: File): List[File] = {
    Path.allSubpaths(f).map(_._1).toList
  }
}
