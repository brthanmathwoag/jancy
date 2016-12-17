lazy val commonSettings = Seq(
  organization := "jancy",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.0",
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  )
)

lazy val jancyCore = project
  .in(file("jancy-core"))
  .settings(commonSettings: _*)

lazy val submodules = TaskKey[Unit]("submodules", "Initialize submodules")

lazy val submodulesSettings =
  submodules := {
    streams.value.log.info("Testing if submodules should be initialized")
    val submodulesFiles = file("submodules").listFiles
    if (submodulesFiles == null || submodulesFiles.isEmpty || submodulesFiles.exists(_.listFiles.isEmpty)) {
      streams.value.log.info("Empty dir found, initializing submodules ...")
      Seq("git", "submodule", "init").!
      Seq("git", "submodule", "update").!
    }
  }

lazy val jancyModulesGen = project
  .in(file("jancy-modulesgen"))
  .dependsOn(jancyCore)
  .settings(commonSettings: _*)
  .settings(
    mainClass in Compile := Some("jancy.modulesgen.Main"),
    libraryDependencies ++= Seq(
      "org.yaml" % "snakeyaml" % "1.17",
      "com.github.jknack" % "handlebars" % "4.0.6",
      "com.jsuereth" %% "scala-arm" % "2.0"
    ),
    submodulesSettings,
    compile in Compile := (compile in Compile).dependsOn(submodules).value
  )

lazy val generateSources = TaskKey[Seq[java.io.File]]("generateSources", "Generate sources")

lazy val generateSourcesSettings =
  generateSources := {
    streams.value.log.info("Testing if jancy-modules should be regenerated")

    def getLastModificationDate(f: File): Long =
      if (f.exists) maxOrZero(getFilesRecursively(f).map(_.lastModified))
      else 0

    def maxOrZero(xs: Seq[Long]) = if (xs.isEmpty) 0.toLong else xs.max

    def getFilesRecursively(f: File): List[File] = {
      val subitems = f.listFiles.toList
      subitems ++ subitems.filter(_.isDirectory).flatMap(getFilesRecursively)
    }

    val modulesGenLastModified = getLastModificationDate(file("jancy-modulesgen/src"))
    val modulesLastModified = getLastModificationDate(file("jancy-modules/src"))
    val submodulesLastModified = getLastModificationDate(file("submodules"))

    if (modulesGenLastModified >= modulesLastModified || submodulesLastModified > modulesLastModified) {
      streams.value.log.info("jancy-modulesgen changed, regenerating jancy-modules sources ...")

      Seq("rm", "-rf", "jancy-modules/src/*").!

      (testLoader in Test in jancyModulesGen)
        .value
        .loadClass("jancy.modulesgen.Main")
        .getMethod("main", Array[String]().getClass)
        .invoke(null, Array[String]())

      getFilesRecursively(file("jancy-modules/src")).filter(_.getName.endsWith(".java")).map(_.getAbsoluteFile)
    } else Seq[java.io.File]()
  }

lazy val jancyModules = project
  .in(file("jancy-modules"))
  .dependsOn(jancyCore)
  .settings(commonSettings: _*)
  .settings(
    generateSourcesSettings,
    sourceGenerators in Compile += generateSources.taskValue,
    cleanFiles += file("jancy-modules/src")
  )

lazy val jancyTranspiler = project
  .in(file("jancy-transpiler"))
  .dependsOn(jancyModules)
  .settings(commonSettings: _*)
