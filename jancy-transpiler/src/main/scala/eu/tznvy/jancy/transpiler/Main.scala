package eu.tznvy.jancy.transpiler

import java.nio.file.Paths

import eu.tznvy.jancy.transpiler.argparsing.ArgsParser
import eu.tznvy.jancy.transpiler.discovery.{ConfigurationFactoriesDiscoverer, ContentFilesDiscoverer, ContentFilesExtractor}
import eu.tznvy.jancy.transpiler.helpers.ConcreteFilesystem
import eu.tznvy.jancy.transpiler.rendering.ConfigurationRenderer

object Main {
  def main(args: Array[String]): Unit = {
    ArgsParser.tryParse(args).map({ programArgs =>

      val filesystem = new ConcreteFilesystem()
      val configurationRenderer = new ConfigurationRenderer(filesystem)
      val contentFilesExtractor = new ContentFilesExtractor(filesystem)

      ConfigurationFactoriesDiscoverer
        .getConfigurationFactoriesInJar(programArgs.jar)
        .foreach({ cf =>
          val configuration = cf.build
          val outputPath = Paths.get(programArgs.output.getPath, configuration.getName)

          configurationRenderer.render(configuration, outputPath)

          val contentFiles = ContentFilesDiscoverer.discover(programArgs.jar, configuration.getName)
          contentFilesExtractor.extract(contentFiles, programArgs.jar, outputPath)
        })

      true
    }).getOrElse(System.exit(1))
  }
}
