package jancy.modulesgen.codegeneration

import com.github.jknack.handlebars.context.FieldValueResolver
import com.github.jknack.handlebars.{Template, Context, Handlebars}
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import jancy.modulesgen.model.ModuleMetadata

object ClassGenerator {

  def generateClass(moduleMetadata: ModuleMetadata): String =
    template(createContext(moduleMetadata))

  private def createContext(moduleMetadata: ModuleMetadata) =
    Context
      .newBuilder(buildModelForHandlebars(moduleMetadata))
      //workaround to access properties without javabean getters/setters
      .resolver(FieldValueResolver.INSTANCE)
      .build()

  private lazy val template: Template =
    new Handlebars(
      new ClassPathTemplateLoader("/templates")
    ).compile("Class")

  private def buildModelForHandlebars(moduleMetadata: ModuleMetadata): HandlebarsModule =
    HandlebarsModule(
      moduleMetadata.className,
      moduleMetadata.originalName,
      moduleMetadata.namespace,
      //TODO: refactor me
      formatJavadoc(
        moduleMetadata
          .description
          .getOrElse(moduleMetadata
              .shortDescription
              .getOrElse(s"This is a wrapper for ${moduleMetadata.originalName} module")),
        false),
      moduleMetadata.options.map({ o =>
        HandlebarsOption(
          o.name,
          o.originalName,
          formatJavadoc(
            o.description
              .getOrElse(s"This is a wrapper for ${o.originalName} parameter"),
            true)
      )}).toArray
    )

  private def formatJavadoc(text: String, isMemberJavadoc: Boolean): String = {
    val indentation = if (isMemberJavadoc) 4 else 0
    val maxLineLength = 80 - indentation - " * ".length

    indentText(javadocify(wrapTextAround(insertParagraphs(text).split('\n'), maxLineLength)), indentation).mkString("\n")
  }


  private def wrapTextAround(lines: Seq[String], maxLineLength: Int): Seq[String] =
    lines
      .flatMap({ l =>
        val words = l.split(' ')
        words.foldLeft(List(""))({ (acc: List[String], w: String) =>
          val currentLine = acc.head
          val canAppend = (w.length + currentLine.length + 1) < maxLineLength
          if (canAppend) (currentLine + " " + w).trim :: acc.tail
          else w :: acc
      }).reverse
    })

  private def javadocify(lines: Seq[String]): Seq[String] =
    Seq("/**") ++ lines.map(" * " + _) ++ Seq(" */")

  private def indentText(lines: Seq[String], indentation: Int): Seq[String] =
    lines.map((" " * indentation) + _)

  private def insertParagraphs(text: String): String =
    text.replace("\n", "\n<p>\n")

  private case class HandlebarsModule(
    name: String,
    originalName: String,
    namespace: String,
    javadoc: String,
    options: Array[HandlebarsOption]
  )

  private case class HandlebarsOption(
    name: String,
    originalName: String,
    javadoc: String
  )

  private case class HandlebarsModifier(
    name: String,
    originalName: String
  )
}
