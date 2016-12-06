package jancy.modulesgen.discovery

import java.io.File

import jancy.modulesgen.helpers.CapitalizationHelper
import jancy.modulesgen.model.{OptionMetadata, ModuleMetadata}
import org.yaml.snakeyaml.Yaml

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
    val name = resolveModuleName(file)
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
    val content =
      Source
        .fromFile(file)
        .getLines()
        .dropWhile(l => !l.startsWith("DOCUMENTATION = '''"))
        .drop(1)
        .takeWhile(l => l != "'''")
        .mkString("\n")
        .replace("\\\n", "")  //multi-line python string escapes
        .replace("\\\\", "\\")
    //TODO: escape \w\([^\)]+\) -- C(...), etc

    new Yaml().load(content)
  }

  private def resolveNamespace(file: File): String =
    //TODO: will break if the path doesn't start with 'submodules/'
    "jancy.modules." + file
      .getPath
      .split(File.separatorChar)
      .drop(2)
      .init
      .map(_.replace("_", ""))
      .mkString(".")


  private def resolveModuleName(file: File): String = {
    //TODO: a terrible hack; check if there are stdlib methods for this
    val filenameStart = file.getParent.length + 1
    val extensionStart = file.getPath.length - 3
    file.getPath.substring(filenameStart, extensionStart)
  }

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
        val name = CapitalizationHelper.snakeCaseToCamelCase(originalName)
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

  private def resolveDescription(maybeNode: Option[Any]): Option[String] =
    maybeNode flatMap resolveDescription

  private def resolveDescription(node: Any): Option[String] =
    node match {
      case multiline: java.util.List[String] => Some(multiline.asScala.toList.mkString("\n"))
      case oneline: String => Some(oneline)
      case _ => None
    }
}
