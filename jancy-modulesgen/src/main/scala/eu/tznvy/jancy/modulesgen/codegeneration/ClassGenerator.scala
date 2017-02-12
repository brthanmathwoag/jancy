package eu.tznvy.jancy.modulesgen.codegeneration

import java.util.regex.Pattern

import com.github.jknack.handlebars.context.FieldValueResolver
import com.github.jknack.handlebars.{Context, Handlebars, Template}
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import eu.tznvy.jancy.modulesgen.model.{Choices, ModuleMetadata, OptionAliasMetadata, OptionMetadata}

/**
  * Generates wrapper source code based on ModuleMetadata via Handlebars
  */
object ClassGenerator {

  private lazy val deprecationPattern = Pattern.compile("deprecated", Pattern.CASE_INSENSITIVE)

  def generateClass(moduleMetadata: ModuleMetadata): String =
    template(createContext(moduleMetadata))

  private lazy val template: Template =
    new Handlebars(
      new ClassPathTemplateLoader("/templates")
    ).compile("Class")

  private def createContext(moduleMetadata: ModuleMetadata): Context =
    Context
      .newBuilder(buildModelForHandlebars(moduleMetadata))
      //workaround to access properties without javabean getters/setters
      .resolver(FieldValueResolver.INSTANCE)
      .build()

  private def buildModelForHandlebars(moduleMetadata: ModuleMetadata): HandlebarsModule =
    HandlebarsModule(
      moduleMetadata.className,
      moduleMetadata.originalName,
      moduleMetadata.namespace,
      formatJavadoc(
        getHandlebarsModuleDescription(moduleMetadata),
        isMemberJavadoc = false),
      moduleMetadata
        .options
        .filter(_.originalName != "free_form")
        .flatMap(buildHandlebarsSetters(moduleMetadata.className))
        .groupBy({ o => (o.name, o.getClass) })
        .map(_._2.head)
        .toArray
        .sortBy(_.name),
      moduleMetadata
        .options
        .filter({ o => o.choices.isDefined && o.choices.flatMap(pickBoolValues).isEmpty })
        .map(_.choices.get)
        .map(buildHandlebarsChoices)
        .toArray,
      moduleMetadata.options.exists(_.originalName == "free_form"),
      moduleMetadata.deprecated.nonEmpty
    )

  private def buildHandlebarsSetters(className: String)(o: OptionMetadata): List[HandlebarsSetter] = {
    val description = formatJavadoc(
      getHandlebarsOptionDescription(o),
      isMemberJavadoc = true)

    (OptionAliasMetadata(o.name, o.originalName) :: o.aliases.toList)
      .flatMap({ alias =>
        val isDeprecated = o.description.exists({ d => deprecationPattern.matcher(d).find })

        val stringSetter =
          Some(
            StringSetter(
              alias.name,
              alias.originalName,
              description,
              className,
              isDeprecated
            )
          )

        val boolValues = o.choices.flatMap(pickBoolValues)

        val enumSetter = o.choices
          .filter({ cs => boolValues.isEmpty })
          .map({ cs =>
            EnumSetter(
              alias.name,
              alias.originalName,
              description,
              className,
              isDeprecated,
              cs.name
            )
          })

        val boolSetter = boolValues.map({ case (trueValue, falseValue) =>
          BoolSetter(
            alias.name,
            alias.originalName,
            description,
            className,
            isDeprecated,
            trueValue,
            falseValue
          )
        })

        List(stringSetter, enumSetter, boolSetter)
          .collect({ case Some(s) => s })
    })
  }

  private def pickBoolValues(choices: Choices): Option[(String, String)] = {
    val trueStrings = Seq("yes", "1", "true")
    val falseStrings = Seq("no", "0", "false")

    val trueValue =
      choices
        .choices
        .map(_.originalName)
        .collectFirst({ case s if trueStrings.contains(s) => s })

    val falseValue =
      choices
        .choices
        .map(_.originalName)
        .collectFirst({ case s if falseStrings.contains(s) => s })

    trueValue.flatMap({ t => falseValue.map({ f => (t, f) }) })
  }

  private def buildHandlebarsChoices(cs: Choices): HandlebarsChoices =
    HandlebarsChoices(
      cs.name,
      choices = cs.choices.map({ c =>
        HandlebarsChoice(
          c.name,
          c.originalName)
      }).toArray
    )

  private def getHandlebarsOptionDescription(o: OptionMetadata): String = {
    val description = o.description.getOrElse(s"This is a wrapper for ${o.originalName} parameter")
    val deprecated = if (deprecationPattern.matcher(description).find) "@deprecated" else ""
    Seq(description, deprecated).filter(_.nonEmpty).mkString("\n")
  }

  private def getHandlebarsModuleDescription(moduleMetadata: ModuleMetadata): String = {

    val description = moduleMetadata
      .description
      .getOrElse(
        moduleMetadata
          .shortDescription
          .getOrElse(s"This is a wrapper for ${moduleMetadata.originalName} module"))

    val authors =
      if (moduleMetadata.authors.nonEmpty) "Authors: " + moduleMetadata.authors.mkString(", ")
      else ""

    val versionAdded = moduleMetadata.versionAdded.map("Version added: " + _).getOrElse("")

    val notes = moduleMetadata.notes.mkString("\n")

    val deprecated = moduleMetadata.deprecated.map("@deprecated " + _).getOrElse("")

    Seq(description, notes, authors, versionAdded, deprecated).filter(_.nonEmpty).mkString("\n")
  }

  private def formatJavadoc(text: String, isMemberJavadoc: Boolean): String = {
    val indentation = if (isMemberJavadoc) 4 else 0
    val maxLineLength = 80 - indentation - " * ".length

    indentText(
      javadocify(
        wrapTextAround(
          insertParagraphs(text).split('\n'),
          maxLineLength)),
      indentation
    ).mkString("\n")
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
    options: Array[HandlebarsSetter],
    choices: Array[HandlebarsChoices],
    isFreeform: Boolean,
    isDeprecated: Boolean
  )

  private case class StringSetter(
    override val name: String,
    override val originalName: String,
    override val javadoc: String,
    override val className: String,
    override val isDeprecated: Boolean
  ) extends HandlebarsSetter(
    name, originalName, javadoc, className, isDeprecated
  )

  private case class EnumSetter(
    override val name: String,
    override val originalName: String,
    override val javadoc: String,
    override val className: String,
    override val isDeprecated: Boolean,
    typeName: String
  ) extends HandlebarsSetter(
    name, originalName, javadoc, className, isDeprecated
  )

  private case class BoolSetter(
    override val name: String,
    override val originalName: String,
    override val javadoc: String,
    override val className: String,
    override val isDeprecated: Boolean,
    trueValue: String,
    falseValue: String
  ) extends HandlebarsSetter(
    name, originalName, javadoc, className, isDeprecated
  )

  private class HandlebarsSetter(
    val name: String,
    val originalName: String,
    val javadoc: String,
    val className: String,
    val isDeprecated: Boolean
  ) {
    val setterType = getClass.getSimpleName
  }

  private case class HandlebarsModifier(
    name: String,
    originalName: String
  )

  private case class HandlebarsChoices(
    name: String,
    choices: Array[HandlebarsChoice]
  )

  private case class HandlebarsChoice(
    name: String,
    originalName: String
  )
}
