import sbt._
import sbt.Keys._

object Tasks {

  def initializeSubmodules(logger: Logger): Unit = {
    logger.info("Testing if submodules should be initialized")

    val submodulesFiles = file("submodules").listFiles
    
    if (submodulesFiles == null
        || submodulesFiles.isEmpty
        || submodulesFiles.exists(_.listFiles.isEmpty)) {

      logger.info("Empty dir found, initializing submodules ...")
      Seq("git", "submodule", "init").!
      Seq("git", "submodule", "update").!
    }
  }

  def generateSources(logger: Logger, classLoader: ClassLoader): Seq[File] = {
    
    logger.info("Testing if jancy-modules should be regenerated")
    
    def getLastModificationDate(f: File): Long =
      if (f.exists) maxOrZero(Path.allSubpaths(f).map(_._1.lastModified))
      else 0

    def maxOrZero(xs: Traversable[Long]) = if (xs.isEmpty) 0.toLong else xs.max

    val modulesGenLastModified = getLastModificationDate(file("jancy-modulesgen/src"))
    val modulesLastModified = getLastModificationDate(file("jancy-modules/src"))
    val submodulesLastModified = getLastModificationDate(file("submodules"))

    if (modulesGenLastModified >= modulesLastModified
        || submodulesLastModified > modulesLastModified) {

      logger.info("jancy-modulesgen changed, regenerating jancy-modules sources ...")

      Seq("rm", "-rf", "jancy-modules/src/*").!

      classLoader
        .loadClass("eu.tznvy.jancy.modulesgen.Main")
        .getMethod("main", Array[String]().getClass)
        .invoke(null, Array[String]())

      Path.allSubpaths(file("jancy-modules") / "src")
        .filter(_._2.endsWith(".java"))
        .map(_._1.getAbsoluteFile)
        .toSeq
    } else Seq[File]()
  }
}

