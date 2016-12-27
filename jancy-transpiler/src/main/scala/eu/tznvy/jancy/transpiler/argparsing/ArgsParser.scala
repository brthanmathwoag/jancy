package eu.tznvy.jancy.transpiler.argparsing

import java.io.File
import scala.util.{Try, Success, Failure}

import org.apache.commons.cli.{Option, Options, DefaultParser, CommandLine, HelpFormatter}

object ArgsParser {
  private val defaultOutput = "."
  
  def tryParse(args: Array[String]): scala.Option[Args] = {
    val jarOption = Option
      .builder("j")
      .longOpt("jar")
      .desc("The path to a jar file containing the configuration.")
      .hasArg
      .argName("/path/to/configuration.jar")
      .required
      .build
      
     val outputOption = Option
       .builder("o")
       .longOpt("output")
       .desc("The directory where the ansible configuration will be saved. Defaults to current directory.")
       .hasArg
       .argName("/output/path/")
       .build
  
    val options = new Options
    options.addOption(jarOption)
    options.addOption(outputOption)
    val parser = new DefaultParser()
    
    Try {
      val commandLine = parser.parse(options, args)
      Args(
        new File(
          commandLine.getOptionValue(jarOption.getOpt)),
        new File(
          scala.Option(commandLine.getOptionValue(outputOption.getOpt))
            .getOrElse(defaultOutput))
      )
    } match {
      case Success(a) => Some(a)
      case Failure(e) => {
        println(e)
        println(e.getMessage)
        println()
        val formatter = new HelpFormatter()
        //TODO: get filename from the filename
        formatter.printHelp("jancy-transpiler", options)
        None
      }
    }
  }
}
