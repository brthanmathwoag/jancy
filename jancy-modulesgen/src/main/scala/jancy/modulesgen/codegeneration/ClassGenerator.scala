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

    val moduleClass = ModuleClass(
      moduleMetadata.namespace,
      moduleMetadata.className,
      moduleMetadata.description.getOrElse(moduleMetadata.shortDescription.getOrElse(""))
    )

    val context = Context
      .newBuilder(moduleClass)
      .resolver(FieldValueResolver.INSTANCE)  //workaround to access properties without javabean getters/setters
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

  private case class ModuleClass(
    namespace: String,
    name: String,
    javadoc: String
  )
}
