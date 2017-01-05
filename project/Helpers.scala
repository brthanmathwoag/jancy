import sbt._
import sbt.Keys._
import scala.xml.{Elem, Comment, Node, NodeSeq}
import scala.xml.transform.{RewriteRule, RuleTransformer}

object Helpers {
  def getFilesRecursively(f: File): List[File] = {
    Path.allSubpaths(f).map(_._1).toList
  }

  def dropIfDependency(node: Node): Node = {
      new RuleTransformer(new RewriteRule {
        override def transform(node: Node): NodeSeq = node match {
          case e: Elem if e.label == "dependency" => NodeSeq.Empty
          case _ => node
        }
      }).transform(node).head
    }
}
