package eu.tznvy.jancy.transpiler

import java.nio.file.Paths
import resource._

import eu.tznvy.jancy.transpiler.argparsing.{ArgsParser, PrintUsageArgs, PrintVersionArgs, TranspileArgs}
import eu.tznvy.jancy.transpiler.discovery.{PlaybookFactoriesDiscoverer, ContentFilesDiscoverer, ContentFilesExtractor, JarClassSource}
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

    val maybePlaybookFactories =
      managed(new JarClassSource(args.jar))
        .map(PlaybookFactoriesDiscoverer.findPlaybookFactories(_, args.classname))
        .either

    if (maybePlaybookFactories.isLeft)
      throw maybePlaybookFactories.left.get.head

    val playbookFactories = maybePlaybookFactories.right.get

    if (playbookFactories.isEmpty) {
      val errorMessage =
        args
          .classname
          .map(_ + " not found or it does not implement PlaybookFactory interface.")
          .getOrElse("No PlaybookFactory implementations found in the jar.")

      throw new Error(errorMessage)
    }

    val playbooks =
      playbookFactories
        .map({ pbf =>
          val playbook = pbf.build
          val outputPath = Paths.get(args.output.getPath, playbook.getName)
          val contentFiles = ContentFilesDiscoverer.discover(args.jar, playbook.getName)
          (playbook, outputPath, contentFiles)
        })

    playbooks.foreach({ case (playbook, outputPath, contentFiles) =>
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
