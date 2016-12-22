package jancy.transpiler

import java.nio.file.Paths

import jancy.transpiler.argparsing.ArgsParser
import jancy.transpiler.discovery.ConfigurationFactoriesDiscoverer
import jancy.transpiler.rendering.ConfigurationRenderer

object Main {
  def main(args: Array[String]): Unit = {
    ArgsParser.tryParse(args).map({ programArgs =>
      
      ConfigurationFactoriesDiscoverer
        .getConfigurationFactoriesInJar(programArgs.jar)
        .foreach({ cf =>
          val configuration = cf.build
          val outputPath = Paths.get(programArgs.output.getPath, cf.getName)
          ConfigurationRenderer.render(configuration, outputPath)
        })
    })
  }
}
