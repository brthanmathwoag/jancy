package eu.tznvy.jancy.transpiler

import java.nio.file.Paths

import eu.tznvy.jancy.transpiler.argparsing.{ArgsParser, PrintVersionArgs, TranspileArgs}
import eu.tznvy.jancy.transpiler.discovery.{ConfigurationFactoriesDiscoverer, ContentFilesDiscoverer, ContentFilesExtractor}
import eu.tznvy.jancy.transpiler.helpers.ConcreteFilesystem
import eu.tznvy.jancy.transpiler.rendering.ConfigurationRenderer

object Main {
  private val executableName = "jancy"
  private val version = "0.1.0-SNAPSHOT"

  def main(args: Array[String]): Unit = {
    new ArgsParser(executableName)
      .tryParse(args)
      .map({
        case a: TranspileArgs => transpile(a)
        case PrintVersionArgs => printVersion
    }).getOrElse(System.exit(1))
  }

  def transpile(args: TranspileArgs): Boolean = {
    val filesystem = new ConcreteFilesystem()
    val configurationRenderer = new ConfigurationRenderer(filesystem)
    val contentFilesExtractor = new ContentFilesExtractor(filesystem)

    ConfigurationFactoriesDiscoverer
      .getConfigurationFactoriesInJar(args.jar)
      .foreach({ cf =>
        val configuration = cf.build
        val outputPath = Paths.get(args.output.getPath, configuration.getName)

        configurationRenderer.render(configuration, outputPath)

        val contentFiles = ContentFilesDiscoverer.discover(args.jar, configuration.getName)
        contentFilesExtractor.extract(contentFiles, args.jar, outputPath)
      })
    true
  }

  def printVersion: Boolean = {
    println(s"$executableName $version")
    true
  }
}
