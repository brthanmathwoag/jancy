package eu.tznvy.jancy.modulesgen.discovery

import java.io.File
import java.util.regex.Pattern

import eu.tznvy.jancy.modulesgen.helpers.{CapitalizationHelper, DocumentMerger}
import eu.tznvy.jancy.modulesgen.discovery.model.{Choice, Choices, ModuleMetadata, OptionAliasMetadata, OptionMetadata}
import eu.tznvy.jancy.modulesgen.specialcases.{SpecialCases, SpecialCase}
import org.yaml.snakeyaml.Yaml
import resource._

import scala.collection.JavaConverters._
import scala.io.Source

/**
  * Reads module metadata from a module file
  */
object MetadataReader {

  def readModuleMetadata(file: File): ModuleMetadata = {
    val documentation = assembleDocumentation(file)
    val namespace = resolveNamespace(file)
    val name = navigate[String](documentation, List("module")).get
    val className = CapitalizationHelper.snakeCaseToPascalCase(name)
    val description = resolveDescription(navigate[String](documentation, List("description")))
    val shortDescription = resolveDescription(navigate[String](documentation, List("short_description")))
    val specialCase = SpecialCases.get(name)
    val documentationFragments = castAsSeq(navigate[Any](documentation, List("extends_documentation_fragment")))
    val authors = castAsSeq(navigate[Any](documentation, List("author")))
    val options = readOptions(specialCase, documentation)
    val versionAdded = navigate[Any](documentation, List("version_added")).map(_.toString).headOption
    val notes = castAsSeq(navigate[Any](documentation, List("notes")))
    val deprecated = navigate[Any](documentation, List("deprecated")).map(_.toString).headOption

    ModuleMetadata(
      className,
      name,
      namespace,
      description,
      shortDescription,
      options,
      documentationFragments,
      authors,
      versionAdded,
      notes,
      deprecated
    )
  }

  private def assembleDocumentation(file: File): Any = {
    val base = readDocumentation(file).asInstanceOf[java.util.LinkedHashMap[String, Object]]
    val fragmentNames = castAsSeq(navigate[Any](base, List("extends_documentation_fragment")))
    val assembledDocumentation = fragmentNames
      .foldLeft(base)({ (current, fragmentName) =>
        val (fragmentFilename, section) = fragmentName.split('.') match {
          case Array(a, b) => (a, b.toUpperCase)
          case Array(a) => (a, "DOCUMENTATION")
        }
        val fragmentPath = "submodules/ansible/lib/ansible/utils/module_docs_fragments/" + fragmentFilename + ".py"
        val fragment = readDocumentation(new File(fragmentPath), section).asInstanceOf[java.util.LinkedHashMap[String, Object]]
        val merged = DocumentMerger.merge(current, fragment)
        merged
      })
    assembledDocumentation
  }

  private def readDocumentation(file: File, section: String = "DOCUMENTATION"): Any = {
    val content = managed(Source.fromFile(file))
      .map(_
        .getLines
        .dropWhile(!isDocumentationStart(_, section))
        .drop(1)
        .takeWhile(!isDocumentationEnd(_))
        .mkString("\n")
        .replace("\\\n", "")  //multi-line python string escapes
        .replace("\\\\", "\\")
        .replace("\\'", "'"))
      .opt
      .getOrElse("")
    //TODO: escape \w\([^\)]+\) -- C(...), etc

    new Yaml().load(content)
  }

  private def isDocumentationStart(line: String, section: String): Boolean =
    Pattern.matches("^\\s*" + section + "\\s*=\\s*[ur]?['\"]{3}.*", line)

  private def isDocumentationEnd(line: String): Boolean =
    Pattern.matches("^\\s*['\"]{3}.*", line)

  private def resolveNamespace(file: File): String =
    //TODO: this expects the paths to start with 'submodules/ansible/lib/ansible/modules/'
    "eu.tznvy.jancy.modules." + file
      .getPath
      .split(File.separatorChar)
      .drop(5)
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

  private def readOptions(specialCase: Option[SpecialCase], documentation: Any): Seq[OptionMetadata] = {
    val maybeOptions = navigate[java.util.Map[String, Object]](documentation, List("options"))

    maybeOptions.map({ os =>
      os.asScala.map({ o =>

        val originalName = o._1

        val name = (escapeOptionName _)
          .andThen(CapitalizationHelper.snakeCaseToCamelCase)
          .andThen(escapeJavaKeywords)(originalName)

        val description = resolveDescription(navigate[String](o._2, List("description")))
          .map(escapeEndOfComment)

        val default = navigate[String](o._2, List("default"))

        val required = navigate[Boolean](o._2, List("required")).getOrElse(false)

        val choicesEnumName = CapitalizationHelper.camelCaseToPascalCase(name)

        val choices = navigate[java.util.List[_]](o._2, List("choices"))
          .map({ cs => cs.asScala.map({ c => c.toString }).filter(!_.trim.isEmpty) })
          .flatMap({ cs =>
            if (specialCase.exists(_.optionsWithNoEnum.contains(originalName))) None
            else Some(Choices(
              choicesEnumName,
              if (specialCase.exists(_.optionsWithCustomEnumBuilder.contains(originalName)))
                specialCase.get.optionsWithCustomEnumBuilder(originalName)(cs)
              else cs
                .toList
                .map({ c =>
                  if (c.trim == "") None
                  else Some(
                    Choice(
                      (CapitalizationHelper.snakeCaseToAllCaps _)
                        .andThen(escapeEnumValueName)(c),
                      c))
                })
                .collect({ case Some(c) => c })
                .groupBy(_.name)
                .map(_._2.head)
                .toSeq
            ))
          })

        val aliases = navigate[java.util.List[String]](o._2, List("aliases"))
          .map(_.asScala.toList)
          .getOrElse(List())
          .map({ originalName =>
            val name = (escapeOptionName _)
              .andThen(CapitalizationHelper.snakeCaseToCamelCase)
              .andThen(escapeJavaKeywords)(originalName)
            OptionAliasMetadata(
              name,
              originalName)
          })

        OptionMetadata(
          name,
          originalName,
          required,
          description,
          default,
          choices,
          aliases)
      }).toList
    }).getOrElse(Seq[OptionMetadata]())
  }

  private def escapeEnumValueName(name: String): String = {
    val escaped = name
      //workaround for cloud.profitbricks.ProfibricksDatacenter
      .replace("/", "_")
      //workaround for cloud.amazon.RdsParamGroup
      .replace(".", "_")
      //workaround for RaxMonCheck
      .replace("-", "_")
      //workaround for DatadogMonitor
      .replace(" ", "_")
      //workaround for RdsParamGroup
      .replace("'", "")
      //workaround for YumRepository
      .replace(":", "_")
      //workaround for GlusterVolume
      .replace(",", "_")

    if(escaped.head.isDigit) "_" + escaped
    else escaped
  }

  private def escapeOptionName(name: String): String = {
    val escaped = name
      //workaround for storage.netapp.NetappEHostgroup
      .replace("-", "_")
      //workaround for packaging.os.Urpmi
      .replace(":", "")
      //workaround for DockerLogin
      .replace(".", "_")

    if (escaped.head.isDigit) "a" + escaped
    else escaped
  }

  private def escapeEndOfComment(s: String): String =
    //workaround for system.cron
    s.replace("*/", "*&#47;")

  private def escapeJavaKeywords(name: String): String =
    if (Set(
      "public", "default", "interface", "private", "switch", "goto", "package",
      "if", "static"
    ).contains(name)) name + "_"
    else name

  private def resolveDescription(maybeNode: Option[Any]): Option[String] =
    maybeNode flatMap resolveDescription

  private def resolveDescription(node: Any): Option[String] =
    node match {
      case multiline: java.util.List[_] => Some(multiline.asScala.toList.mkString("\n"))
      case oneline: String => Some(oneline)
      case _ => None
    }

  private def castAsSeq(maybeNode: Option[Any]): Seq[String] =
    maybeNode match {
      case Some(v) => v match {
        case one: String => Seq(one)
        case many: java.util.List[_] => many.asScala.map(_.toString)
        case _ => Seq[String]()
      }
      case _ => Seq[String]()
    }
}
