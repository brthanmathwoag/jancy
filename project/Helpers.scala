import sbt._
import sbt.Keys._

import scala.xml.{Comment, Elem, Node, NodeSeq}
import scala.xml.transform.{RewriteRule, RuleTransformer}

object Helpers {

  def dropIfDependency(node: Node): Node = {
      new RuleTransformer(new RewriteRule {
        override def transform(node: Node): NodeSeq = node match {
          case e: Elem if e.label == "dependency" => NodeSeq.Empty
          case _ => node
        }
      }).transform(node).head
    }

  def pickFilesFromSubpaths(
      roots: Seq[String],
      subpath: File,
      predicate: String => Boolean): Seq[(File, String)] = {

    roots
      .map(file(_) / subpath.toString)
      .flatMap({ f =>
        Path
          .allSubpaths(f)
          .filter({ case (_, relativePath) => predicate(relativePath) })
          .toSeq
      })
  }
}
