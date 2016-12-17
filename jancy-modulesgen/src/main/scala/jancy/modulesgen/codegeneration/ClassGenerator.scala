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
      moduleMetadata
        .description
        .getOrElse(moduleMetadata
            .shortDescription
            .getOrElse(s"This is a wrapper for ${moduleMetadata.originalName} module")),
      moduleMetadata.options.map({ o =>
        HandlebarsOption(
          o.name,
          o.originalName,
          o.description
            .getOrElse(s"This is a wrapper for ${o.originalName} parameter")
      )}).toArray
    )

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
    description: String
  )

  private case class HandlebarsModifier(
    name: String,
    originalName: String
  )
}
