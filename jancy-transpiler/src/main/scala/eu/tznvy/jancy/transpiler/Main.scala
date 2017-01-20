package eu.tznvy.jancy.transpiler

import java.nio.file.Paths

import eu.tznvy.jancy.transpiler.argparsing.{ArgsParser, PrintUsageArgs, PrintVersionArgs, TranspileArgs}
import eu.tznvy.jancy.transpiler.discovery.{PlaybookFactoriesDiscoverer, ContentFilesDiscoverer, ContentFilesExtractor}
import eu.tznvy.jancy.transpiler.helpers.ConcreteFilesystem
import eu.tznvy.jancy.transpiler.rendering.PlaybookRenderer

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
    val playbookRenderer = new PlaybookRenderer(filesystem)
    val contentFilesExtractor = new ContentFilesExtractor(filesystem)

    val foundPlaybooks =
      PlaybookFactoriesDiscoverer
        .getPlaybookFactoriesInJar(args.jar)
        .map({ pbf =>
          val playbook = pbf.build
          val outputPath = Paths.get(args.output.getPath, playbook.getName)
          val contentFiles = ContentFilesDiscoverer.discover(args.jar, playbook.getName)
          (playbook, outputPath, contentFiles)
        })

    if (foundPlaybooks.isEmpty) {
      throw new Error("No PlaybookFactory implementations found in the jar.")
    }

    foundPlaybooks.foreach({ case (playbook, outputPath, contentFiles) =>
      playbookRenderer.render(playbook, outputPath)
      contentFilesExtractor.extract(contentFiles, args.jar, outputPath)
    })

    true
  }

  def printVersion: Boolean = {
    println(s"$executableName $version")
    true
  }
}
