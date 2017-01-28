package eu.tznvy.jancy.transpiler.argparsing

import java.io.File
import scala.util.{Try, Success, Failure}

import org.apache.commons.cli.{Option, Options, DefaultParser, CommandLine, HelpFormatter}

/**
  * Builds Args from stringly-typed commandline args
  *
  * @param executableName     printed in usage help string
  */
class ArgsParser(executableName: String) {
  private val defaultOutput = "."
  private val jarArgKey = "j"
  private val outputArgKey = "o"
  private val versionArgKey = "v"
  private val helpArgKey = "h"
  private val classnameArgKey = "c"

  private val examplePlaybookPath = "/path/to/configuration.jar"
  private val exampleOutputPath = "/output/path/"
  private val exampleClassname = "com.example.Class"
  
  def tryParse(args: Array[String]): scala.Option[Args] = {
    val parser = new DefaultParser()
    
    Try {
      parse(parser, args)
    } match {
      case Success(a) => Some(a)
      case Failure(e) => {
        printUsage(e)
        None
      }
    }
  }

  private def parse(parser: DefaultParser, args: Array[String]): Args =
    parser.parse(options, args) match {
      case ShouldPrintVersion() => PrintVersionArgs
      case ShouldTranspile(a) => a
      case ShouldPrintUsage() => PrintUsageArgs
    }

  private object ShouldPrintVersion {
    def unapply(commandLine: CommandLine): Boolean =
      commandLine.hasOption(versionArgKey)
  }

  private object ShouldTranspile {
    def unapply(commandLine: CommandLine): scala.Option[TranspileArgs] =
      if (commandLine.hasOption(jarArgKey))
        Some(
          TranspileArgs(
            new File(commandLine.getOptionValue(jarArgKey)),
            new File(
              scala.Option(commandLine
                .getOptionValue(outputArgKey))
                .getOrElse(defaultOutput)),
              scala.Option(commandLine
                .getOptionValue(classnameArgKey))
          )
        )
      else None
  }

  private object ShouldPrintUsage {
    def unapply(commandLine: CommandLine): Boolean =
      commandLine.getArgs.length == 0 || commandLine.hasOption(helpArgKey)
  }

  private def printUsage(exception: Throwable): Unit = {
    println(exception.getMessage)
    println()
    printUsage()
  }

  def printUsage(): Unit = {
    val formatter = new HelpFormatter()
    formatter.printHelp(
      s"$executableName -$jarArgKey $examplePlaybookPath [-$outputArgKey $exampleOutputPath] [-$classnameArgKey $exampleClassname]",
      options)
  }

  private lazy val options: Options = {
    val options = new Options

    options.addOption(
      Option
        .builder(jarArgKey)
        .longOpt("jar")
        .desc("The path to a jar file containing the playbooks.")
        .hasArg
        .argName(examplePlaybookPath)
        .build)

    options.addOption(
      Option
        .builder(outputArgKey)
        .longOpt("output")
        .desc("The directory where the ansible configuration will be saved. " +
          "Defaults to current directory.")
        .hasArg
        .argName(exampleOutputPath)
        .build)

    options.addOption(
      Option
        .builder(classnameArgKey)
        .longOpt("class")
        .desc("The name of the PlaybookFactory implementation to be transpiled. " +
          "If not set, all PlaybookFactories in the jar will be used.")
        .hasArg
        .argName(exampleClassname)
        .build)

    options.addOption(
      Option
        .builder(versionArgKey)
        .longOpt("version")
        .desc("Prints version information and exits.")
        .build)

    options.addOption(
      Option
        .builder(helpArgKey)
        .longOpt("help")
        .desc("Prints usage information and exits.")
        .build)

    options
  }
}
