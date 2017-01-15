package eu.tznvy.jancy.transpiler

import java.nio.file.Paths

import eu.tznvy.jancy.transpiler.argparsing.{ArgsParser, PrintUsageArgs, PrintVersionArgs, TranspileArgs}
import eu.tznvy.jancy.transpiler.discovery.{ConfigurationFactoriesDiscoverer, ContentFilesDiscoverer, ContentFilesExtractor}
import eu.tznvy.jancy.transpiler.helpers.ConcreteFilesystem
import eu.tznvy.jancy.transpiler.rendering.ConfigurationRenderer

object Main {
  private val executableName = "jancy"
  private val version = "0.1.0-SNAPSHOT"

  def main(args: Array[String]): Unit = {
    val argParser = new ArgsParser(executableName)

    argParser
      .tryParse(args)
      .map({
        case a: TranspileArgs => transpile(a)
        case PrintVersionArgs => printVersion
        case PrintUsageArgs => argParser.printUsage()
      }).getOrElse(System.exit(1))
  }

  def transpile(args: TranspileArgs): Boolean = {
    val filesystem = new ConcreteFilesystem()
    val configurationRenderer = new ConfigurationRenderer(filesystem)
    val contentFilesExtractor = new ContentFilesExtractor(filesystem)

    val foundConfigurations =
      ConfigurationFactoriesDiscoverer
        .getConfigurationFactoriesInJar(args.jar)
        .map({ cf =>
          val configuration = cf.build
          val outputPath = Paths.get(args.output.getPath, configuration.getName)
          val contentFiles = ContentFilesDiscoverer.discover(args.jar, configuration.getName)
          (configuration, outputPath, contentFiles)
        })

    if (foundConfigurations.isEmpty) {
      throw new Error("No ConfigurationFactory implementations found in the jar.")
    }

    foundConfigurations.foreach({ case (configuration, outputPath, contentFiles) =>
      configurationRenderer.render(configuration, outputPath)
      contentFilesExtractor.extract(contentFiles, args.jar, outputPath)
    })

    true
  }

  def printVersion: Boolean = {
    println(s"$executableName $version")
    true
  }
}
