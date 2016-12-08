package jancy.modulesgen.discovery

import java.io.File
import java.util.regex.Pattern

import jancy.modulesgen.helpers.CapitalizationHelper
import jancy.modulesgen.model.{OptionMetadata, ModuleMetadata}
import org.yaml.snakeyaml.Yaml
import resource._

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.Try

/***
  * Reads module metadata from a module file
  */
object MetadataReader {

  def readModuleMetadata(file: File): ModuleMetadata = {
    val documentation = readDocumentation(file)
    val namespace = resolveNamespace(file)
    val name = navigate[String](documentation, List("module")).get
    val className = CapitalizationHelper.snakeCaseToPascalCase(name)
    val description = resolveDescription(navigate[String](documentation, List("description")))
    val shortDescription = resolveDescription(navigate[String](documentation, List("short_description")))
    val options = readOptions(documentation)

    ModuleMetadata(
      name,
      className,
      namespace,
      description,
      shortDescription,
      options)
  }

  private def readDocumentation(file: File): Any = {
    val content = managed(Source.fromFile(file))
      .map(_
        .getLines
        .dropWhile(!isDocumentationStart(_))
        .drop(1)
        .takeWhile(!isDocumentationEnd(_))
        .mkString("\n")
        .replace("\\\n", "")  //multi-line python string escapes
        .replace("\\\\", "\\"))
      .opt
      .getOrElse("")
    //TODO: escape \w\([^\)]+\) -- C(...), etc

    new Yaml().load(content)
  }

  private def isDocumentationStart(line: String): Boolean =
    Pattern.matches("^DOCUMENTATION\\s*=\\s*u?['\"]{3}.*", line)

  private def isDocumentationEnd(line: String): Boolean =
    Pattern.matches("^['\"]{3}.*", line)

  private def resolveNamespace(file: File): String =
    //TODO: will break if the path doesn't start with 'submodules/'
    "jancy.modules." + file
      .getPath
      .split(File.separatorChar)
      .drop(2)
      .init
      .map(_.replace("_", ""))
      .mkString(".")

  //TODO: can throw, also terrible hack, also move to a wrapper
  private def navigate[T](root: Any, path: Seq[String]): Option[T] = {
    val found = path.foldLeft[Any](root)({ (curr: Any, key: String) =>
      val map = curr.asInstanceOf[java.util.Map[String, Object]]
      if (map != null) map.get(key) else null
    }).asInstanceOf[T]
    Option(found)
  }

  private def readOptions(documentation: Any): Seq[OptionMetadata] = {
    val maybeOptions = navigate[java.util.Map[String, Object]](documentation, List("options"))

    maybeOptions map { os =>
      os.asScala map { o =>

        val originalName = o._1
        val name = escapeJavaKeywords(CapitalizationHelper.snakeCaseToCamelCase(fixInconsistencies(originalName)))
        val description = resolveDescription(navigate[String](o._2, List("description")))
        val default = navigate[String](o._2, List("default"))
        val required = navigate[Boolean](o._2, List("required")).getOrElse(false)

        val choices = navigate[java.util.List[String]](o._2, List("choices"))
          .map(_.asScala)
          .toList
          .map(_.toString)

        OptionMetadata(
          originalName,
          required,
          description,
          default,
          choices,
          name)
      } toList
    } getOrElse Seq[OptionMetadata]()
  }

  private def fixInconsistencies(name: String): String =
    name
      //workaround for storage.netapp.NetappEHostgroup
      .replace("-", "_")
      //workaround for packaging.os.Urpmi
      .replace(":", "")

  private def escapeJavaKeywords(name: String): String =
    if (Set("public", "default", "interface", "private", "switch", "goto", "package").contains(name)) name + "_"
    else name

  private def resolveDescription(maybeNode: Option[Any]): Option[String] =
    maybeNode flatMap resolveDescription

  private def resolveDescription(node: Any): Option[String] =
    node match {
      case multiline: java.util.List[String] => Some(multiline.asScala.toList.mkString("\n"))
      case oneline: String => Some(oneline)
      case _ => None
    }
}
