package jancy.modulesgen.codegeneration

import org.scalatest.FunSpec

import jancy.modulesgen.model.{ModuleMetadata, OptionMetadata}

import scala.annotation.tailrec

class ClassGeneratorSpec extends FunSpec {

  describe ("The ClassGenerator") {

    it ("should format multi-line javadocs with paragraphs") {

      val input =
        """Lorem ipsum dolor sit amet, consectetur adipiscing elit.
          |Quisque massa arcu, semper quis dapibus vel, laoreet at mauris.
          |Etiam sit amet vulputate magna, eget tempus augue.""".stripMargin

      val expected =
        """/**
           | * Lorem ipsum dolor sit amet, consectetur adipiscing elit.
           | * <p>
           | * Quisque massa arcu, semper quis dapibus vel, laoreet at mauris.
           | * <p>
           | * Etiam sit amet vulputate magna, eget tempus augue.
           | */""".stripMargin

      val module = ModuleMetadata(
        "AModule",
        "a_module",
        "test",
        Some(input),
        None,
        List())

      val content = ClassGenerator.generateClass(module)

      val output = findJavadocs(content).head

      assertResult (expected) { output }
    }

    it ("should wrap javadocs around at 80th column") {

      val input1 = """Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque massa arcu, semper quis dapibus vel, laoreet at mauris. Etiam sit amet vulputate magna, eget tempus augue. Maecenas venenatis, lorem eget ornare sollicitudin, ipsum nulla convallis nisi, ut congue ex orci condimentum lectus."""

      val input2 = """Curabitur vel blandit elit. Pellentesque fermentum lectus non eros tempor, ut vestibulum nulla pretium. Integer vel mollis neque. Nunc commodo purus non mollis pellentesque. Aliquam eu nunc nec est posuere viverra non tincidunt metus."""

      val expected = List(
        """/**
          | * Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque massa arcu,
          | * semper quis dapibus vel, laoreet at mauris. Etiam sit amet vulputate magna,
          | * eget tempus augue. Maecenas venenatis, lorem eget ornare sollicitudin, ipsum
          | * nulla convallis nisi, ut congue ex orci condimentum lectus.
          | */""",
        """    /**
          |     * Curabitur vel blandit elit. Pellentesque fermentum lectus non eros
          |     * tempor, ut vestibulum nulla pretium. Integer vel mollis neque. Nunc
          |     * commodo purus non mollis pellentesque. Aliquam eu nunc nec est posuere
          |     * viverra non tincidunt metus.
          |     */"""
      ).map(_.stripMargin)

      val module = ModuleMetadata(
        "AModule",
        "a_module",
        "test",
        Some(input1),
        None,
        List(
          OptionMetadata(
            "anOption",
            "an_option",
            true,
            Some(input2),
            None,
            List())))

      val content = ClassGenerator.generateClass(module)

      val javadocs = findJavadocs(content)

      assertResult (expected) { javadocs }
    }

    it ("should render a placeholder javadoc if the description is empty") {

      val expected = List(
        """/**
          | * This is a wrapper for a_module module
          | */""",
        """    /**
          |     * This is a wrapper for an_option parameter
          |     */"""
      ).map(_.stripMargin)

      val module = ModuleMetadata(
        "AModule",
        "a_module",
        "test",
        None,
        None,
        List(
          OptionMetadata(
            "anOption",
            "an_option",
            true,
            None,
            None,
            List())))

      val content = ClassGenerator.generateClass(module)

      val javadocs = findJavadocs(content)

      assertResult (expected) { javadocs }
    }
  }

  private def findJavadocs(content: String): List[String] = {
    @tailrec
    def loop(javadocs: List[String], text: Seq[String]): List[String] = {
      val start = text.dropWhile(l => !l.trim.startsWith("/**"))
      if (start.isEmpty) javadocs
      else {
        val javadocWithoutLastLine = start.takeWhile(l => !l.trim.startsWith("*/"))
        val lastCommentLine = start.drop(javadocWithoutLastLine.length)
        val javadoc = javadocWithoutLastLine ++ Seq(lastCommentLine.head)
        val rest = lastCommentLine.tail
        loop(javadoc.mkString("\n") :: javadocs, rest)
      }
    }
    loop(List(), content.split("\n")).reverse
  }
}


