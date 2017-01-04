import sbt._
import sbt.Keys._

object Dependencies {
  lazy val snakeyaml = "org.yaml" % "snakeyaml" % "1.17"
  lazy val commonsCli = "commons-cli" % "commons-cli" % "1.3.1"
  lazy val handlebars = "com.github.jknack" % "handlebars" % "4.0.6"
  lazy val scalaArm = "com.jsuereth" %% "scala-arm" % "2.0"

  lazy val scalatest = "org.scalatest" %% "scalatest" % "3.0.1" % "test"
}
