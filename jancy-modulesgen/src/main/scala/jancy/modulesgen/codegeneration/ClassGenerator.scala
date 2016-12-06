package jancy.modulesgen.codegeneration

import java.io.{PrintWriter, File}
import java.nio.file.Path

import com.github.jknack.handlebars.context.FieldValueResolver
import com.github.jknack.handlebars.{Template, Context, Handlebars}
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import jancy.modulesgen.model.ModuleMetadata

object ClassGenerator {

  def generateClass(classesRootPath: Path, moduleMetadata: ModuleMetadata): Unit = {

    val outputDirectory = classesRootPath.resolve(
      moduleMetadata.namespace.replace('.', File.separatorChar))

    val outputFile = outputDirectory.resolve(moduleMetadata.className + ".java")

    val model = buildModelForHandlebars(moduleMetadata)

    val context = Context
      .newBuilder(model)
      //workaround to access properties without javabean getters/setters
      .resolver(FieldValueResolver.INSTANCE)
      .build()

    //TODO: can throw
    outputDirectory.toFile.mkdirs()

    //TODO: resource mgmt
    val pw = new PrintWriter(outputFile.toString)
    pw.print(template(context))
    pw.close()
  }

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
