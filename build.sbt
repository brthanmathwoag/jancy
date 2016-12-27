val snakeyamlVersion = "1.17"
val scalaArmVersion = "2.0"

def getFilesRecursively(f: File): List[File] = {
  val subitems = f.listFiles.toList
  subitems ++ subitems.filter(_.isDirectory).flatMap(getFilesRecursively)
}

lazy val commonSettings = Seq(
  organization := "eu.tznvy",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.0",
  libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.1" % "test"
  ),
  test in assembly := {}
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
    mainClass in Compile := Some("eu.tznvy.jancy.modulesgen.Main"),
    libraryDependencies ++= Seq(
      "org.yaml" % "snakeyaml" % snakeyamlVersion,
      "com.github.jknack" % "handlebars" % "4.0.6",
      "com.jsuereth" %% "scala-arm" % scalaArmVersion
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

    val modulesGenLastModified = getLastModificationDate(file("jancy-modulesgen/src"))
    val modulesLastModified = getLastModificationDate(file("jancy-modules/src"))
    val submodulesLastModified = getLastModificationDate(file("submodules"))

    if (modulesGenLastModified >= modulesLastModified || submodulesLastModified > modulesLastModified) {
      streams.value.log.info("jancy-modulesgen changed, regenerating jancy-modules sources ...")

      Seq("rm", "-rf", "jancy-modules/src/*").!

      (testLoader in Test in jancyModulesGen)
        .value
        .loadClass("eu.tznvy.jancy.modulesgen.Main")
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

lazy val jancyCommon = project
  .in(file("jancy-common"))
  .dependsOn(jancyModules, jancyCore)
  .settings(commonSettings: _*)
  .settings(
    name := "jancy-common",
    artifactName in (Compile, packageBin) := { (scalaVersion: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "-" + module.revision + "." + artifact.extension
    },
   artifactName in (Compile, packageSrc) := { (scalaVersion: ScalaVersion, module: ModuleID, artifact: Artifact) =>
      artifact.name + "-" + module.revision + "-sources." + artifact.extension
    },
    mappings in (Compile, packageBin) ++= {
      Seq("jancy-core", "jancy-modules")
        .map(_ + "/target/scala-2.12/classes")
        .flatMap({ p =>
          getFilesRecursively(file(p))
            .filter(_.getName.endsWith(".class"))
            .map({ f =>
              val output = f.getPath.substring(p.length + 1)
              (f, output)
            })
        })
    },
    mappings in (Compile, packageSrc) ++= {
      Seq("jancy-core", "jancy-modules")
        .map(_ + "/src/main/java")
        .flatMap({ p =>
          getFilesRecursively(file(p))
            .filter(_.getName.endsWith(".java"))
            .map({ f =>
              val output = f.getPath.substring(p.length + 1)
              (f, output)
            })
        })
    }
  )

lazy val jancyTranspiler = project
  .in(file("jancy-transpiler"))
  .dependsOn(jancyModules)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "org.yaml" % "snakeyaml" % snakeyamlVersion,
      "com.jsuereth" %% "scala-arm" % scalaArmVersion,
      "commons-cli" % "commons-cli" % "1.3.1"
    ),
    name := "jancy-transpiler",
    assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(sbtassembly.AssemblyPlugin.defaultShellScript)),
    assemblyJarName in assembly := s"${name.value}"
  )
