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
      moduleMetadata.description.getOrElse(moduleMetadata.shortDescription.getOrElse("")),
      moduleMetadata.options.map({ o =>
        HandlebarsOption(
          o.name,
          o.originalName,
          o.description.getOrElse("")
      )}).toArray,
      commonModifiers
    )

  private case class HandlebarsModule(
    name: String,
    originalName: String,
    namespace: String,
    javadoc: String,
    options: Array[HandlebarsOption],
    modifiers: Array[HandlebarsModifier]
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

  private val commonModifiers =  Array(
    //TODO: clashes with an opiton in Hipchat, Campfire
    //HandlebarsModifier("notify", "notify"),
    HandlebarsModifier("when", "when"),
    HandlebarsModifier("withItems", "with_items"),
    HandlebarsModifier("withNested", "with_nested"),
    HandlebarsModifier("withDict", "with_dict"),
    HandlebarsModifier("withFile", "with_file"),
    HandlebarsModifier("withFileglob", "with_fileglob"),
    HandlebarsModifier("withTogether", "with_together"),
    HandlebarsModifier("withSubelements", "with_subelements"),
    HandlebarsModifier("withSequence", "with_sequence"),
    HandlebarsModifier("withRandomChoice", "with_random_choice"),
    HandlebarsModifier("register", "register"),
    //TODO: clashes with an option in YumRepository, S3
    //HandlebarsModifier("retries", "retries"),
    //TODO: clashes with an option in WaitFor, EcsCluster, EcsService, ConsulSession
    //HandlebarsModifier("delay", "delay"),
    HandlebarsModifier("withFirstFound", "with_first_found"),
    HandlebarsModifier("withLines", "with_lines"),
    HandlebarsModifier("withIndexedItems", "with_indexed_items"),
    HandlebarsModifier("withIni", "with_ini"),
    HandlebarsModifier("withFlattened", "with_flattened"),
    HandlebarsModifier("withInventoryHostnames", "with_inventory_hostnames")
    //TODO: clashes with an option in OsServer, NovaCompute, Rax*
    //HandlebarsModifier("meta", "meta")
    //TODO: nested modifiers in loop_control
  )
}
