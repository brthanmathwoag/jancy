import sbt._
import sbt.Keys._

object Tasks {

  def initializeSubmodules(logger: Logger) = {
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

  def generateSources(logger: Logger, classLoader: ClassLoader) = {
    
    logger.info("Testing if jancy-modules should be regenerated")
    
    def getLastModificationDate(f: File): Long =
      if (f.exists) maxOrZero(Helpers.getFilesRecursively(f).map(_.lastModified))
      else 0

    def maxOrZero(xs: Seq[Long]) = if (xs.isEmpty) 0.toLong else xs.max

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

      Helpers
        .getFilesRecursively(file("jancy-modules/src"))
        .filter(_.getName.endsWith(".java"))
        .map(_.getAbsoluteFile)
    } else Seq[java.io.File]()
  }
}

