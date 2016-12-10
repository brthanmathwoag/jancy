
lazy val commonSettings = Seq(
    organization := "jancy",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := "2.12.0"
)

lazy val jancyCore = project
    .in(file("jancy-core"))
    .settings(commonSettings: _*)

lazy val jancyModulesGen = project
    .in(file("jancy-modulesgen"))
    .dependsOn(jancyCore)
    .settings(commonSettings: _*)
    .settings(
        mainClass in Compile := Some("jancy.modulesgen.Main"),
        libraryDependencies ++= Seq(
          "org.yaml" % "snakeyaml" % "1.17",
          "com.github.jknack" % "handlebars" % "4.0.6",
          "com.jsuereth" %% "scala-arm" % "2.0",
          "org.scalatest" %% "scalatest" % "3.0.1" % "test"
        )
    )

lazy val jancyModules = project
    .in(file("jancy-modules"))
    .dependsOn(jancyModulesGen)
    .settings(commonSettings: _*)

lazy val jancyTranspiler = project
    .in(file("jancy-transpiler"))
    .dependsOn(jancyModules)
    .settings(commonSettings: _*)
