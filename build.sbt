lazy val commonSettings = Seq(
  organization := "eu.tznvy",
  version := "0.1.0-SNAPSHOT",
  scalaVersion := "2.12.1",
  test in assembly := {},
  coverageEnabled in test := true
)

lazy val jancyCore = project
  .in(file("jancy-core"))
  .settings(commonSettings: _*)
  .settings(
    name := "jancy-core",
    crossPaths := false,
    autoScalaLibrary := false
  )

lazy val submodules = TaskKey[Unit]("submodules", "Initialize submodules")

lazy val jancyModulesGen = project
  .in(file("jancy-modulesgen"))
  .dependsOn(jancyCore)
  .settings(commonSettings: _*)
  .settings(
    mainClass in Compile := Some("eu.tznvy.jancy.modulesgen.Main"),
    libraryDependencies ++= Seq(
      Dependencies.snakeyaml,
      Dependencies.handlebars,
      Dependencies.scalaArm,
      Dependencies.scalatest
    ),
    submodules := Tasks.initializeSubmodules(streams.value.log),
    compile in Compile := (compile in Compile).dependsOn(submodules).value
  )

lazy val jancyModules = project
  .in(file("jancy-modules"))
  .dependsOn(jancyCore)
  .settings(commonSettings: _*)
  .settings(
    name := "jancy-modules",
    crossPaths := false,
    autoScalaLibrary := false,
    sourceGenerators in Compile += Def.task {
      Tasks.generateSources(
        streams.value.log,
        (testLoader in Test in jancyModulesGen).value)
    }.taskValue,
    cleanFiles += file("jancy-modules/src")
  )

lazy val jancyCommon = project
  .in(file("jancy-common"))
  .dependsOn(
    jancyModules,
    jancyCore
  )
  .settings(commonSettings: _*)
  .settings(
    name := "jancy-common",
    crossPaths := false,
    autoScalaLibrary := false,
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Test, packageDoc) := false,
    mappings in (Compile, packageBin) ++=
      Helpers.pickFilesFromSubpaths(
        Seq("jancy-core", "jancy-modules"),
        file("target") / "classes",
        _.endsWith(".class")),
    mappings in (Compile, packageSrc) ++=
      Helpers.pickFilesFromSubpaths(
        Seq("jancy-core", "jancy-modules"),
        file("src") / "main" / "java",
        _.endsWith(".java")),
    pomPostProcess := Helpers.dropIfDependency
  )

lazy val jancyTranspiler = project
  .in(file("jancy-transpiler"))
  .dependsOn(jancyModules)
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      Dependencies.snakeyaml,
      Dependencies.scalaArm,
      Dependencies.commonsCli,
      Dependencies.scalatest
    ),
    name := "jancy-transpiler",
    assemblyOption in assembly :=
      (assemblyOption in assembly)
        .value
        .copy(prependShellScript = Some(
          sbtassembly.AssemblyPlugin.defaultShellScript)),
    assemblyJarName in assembly := "jancy",
    artifact in (Compile, assembly) :=
      (artifact in (Compile, assembly))
        .value
        .copy(classifier = Some("assembly")),
    addArtifact(artifact in (Compile, assembly), assembly),
    publishArtifact in (Compile, packageDoc) := false,
    publishArtifact in (Test, packageDoc) := false,
    publishArtifact in (Compile, packageBin) := false,
    publishArtifact in (Test, packageBin) := false,
    publishArtifact in (Compile, packageSrc) := false,
    publishArtifact in (Test, packageSrc) := false,
    pomPostProcess := Helpers.dropIfDependency
  )

TaskKey[Unit]("buildAll", "Build all artifacts") := {
  (packageBin in Compile in jancyCommon).value
  (packageSrc in Compile in jancyCommon).value
  (assembly in jancyTranspiler).value
}

lazy val lampSimpleExample = project
  .in(file("examples/lamp_simple"))
  .dependsOn(jancyCommon)
  .settings(commonSettings: _*)
  .settings(
    crossPaths := false,
    autoScalaLibrary := false
  )

lazy val examples = project
  .aggregate(lampSimpleExample)
