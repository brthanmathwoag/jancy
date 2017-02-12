package eu.tznvy.jancy.modulesgen.codegeneration

import com.github.jknack.handlebars.{Context, Handlebars, Template}
import com.github.jknack.handlebars.context.FieldValueResolver
import com.github.jknack.handlebars.io.ClassPathTemplateLoader
import eu.tznvy.jancy.modulesgen.codegeneration.model.ModuleClass

object HandlebarsRenderer {

  def render(moduleClass: ModuleClass): String =
    template(createContext(moduleClass))

  private lazy val template: Template =
    new Handlebars(
      new ClassPathTemplateLoader("/templates")
    ).compile("Class")

  private def createContext(moduleClass: ModuleClass): Context =
    Context
      .newBuilder(moduleClass)
      //workaround to access properties without javabean getters/setters
      .resolver(FieldValueResolver.INSTANCE)
      .build()
}
