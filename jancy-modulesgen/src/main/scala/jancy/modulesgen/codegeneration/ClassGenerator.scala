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
      moduleMetadata.namespace,
      moduleMetadata.className,
      //TODO: refactor me
      moduleMetadata.description.getOrElse(moduleMetadata.shortDescription.getOrElse("")),
      moduleMetadata.options map { o =>
        HandlebarsOption(
          o.name,
          o.description.getOrElse(""),
          o.originalName
      )} toArray
    )

  private case class HandlebarsModule(
    namespace: String,
    name: String,
    javadoc: String,
    options: Array[HandlebarsOption]
  )

  private case class HandlebarsOption(
    name: String,
    description: String,
    originalName: String
  )
}
