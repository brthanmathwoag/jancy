package eu.tznvy.jancy.modulesgen.codegeneration

import org.scalatest.FunSpec

import eu.tznvy.jancy.modulesgen.discovery.model.{ModuleMetadata, OptionMetadata}

import scala.annotation.tailrec

class ModuleClassFactorySpec extends FunSpec {

  describe ("The ModuleClassFactory") {

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
        List(),
        List(),
        List(),
        None,
        List(),
        None
      )

      val moduleClass = ModuleClassFactory.build(module)

      val output = moduleClass.javadoc

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
            None,
            List())),
        List(),
        List(),
        None,
        List(),
        None
      )

      val moduleClass = ModuleClassFactory.build(module)

      assertResult (expected) { List(moduleClass.javadoc, moduleClass.setters.head.javadoc) }
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
            None,
            List())),
        List(),
        List(),
        None,
        List(),
        None
      )

      val moduleClass = ModuleClassFactory.build(module)

      assertResult (expected) { List(moduleClass.javadoc, moduleClass.setters.head.javadoc) }
    }

    it ("should include authors of the module in the description, if provided") {

      val expected =
        """/**
          | * Lorem ipsum dolor sit amet
          | * <p>
          | * Authors: author1, author2
          | */""".stripMargin

      val module = ModuleMetadata(
        "AModule",
        "a_module",
        "test",
        Some("Lorem ipsum dolor sit amet"),
        None,
        List(),
        List(),
        List("author1", "author2"),
        None,
        List(),
        None
      )

      val moduleClass = ModuleClassFactory.build(module)

      val output = moduleClass.javadoc

      assertResult (expected) { output }
    }

    it ("should include the version of Ansible in which the module was introduced in the description, if provided") {

      val expected =
        """/**
          | * Lorem ipsum dolor sit amet
          | * <p>
          | * Version added: 1.8
          | */""".stripMargin

      val module = ModuleMetadata(
        "AModule",
        "a_module",
        "test",
        Some("Lorem ipsum dolor sit amet"),
        None,
        List(),
        List(),
        List(),
        Some("1.8"),
        List(),
        None
      )

      val moduleClass = ModuleClassFactory.build(module)

      val output = moduleClass.javadoc

      assertResult (expected) { output }
    }

    it ("should include the notes in the description, if provided") {

      val expected =
        """/**
          | * Lorem ipsum dolor sit amet
          | * <p>
          | * The cake is a lie
          | * <p>
          | * You don't bury the survivors
          | */""".stripMargin

      val module = ModuleMetadata(
        "AModule",
        "a_module",
        "test",
        Some("Lorem ipsum dolor sit amet"),
        None,
        List(),
        List(),
        List(),
        None,
        List("The cake is a lie", "You don't bury the survivors"),
        None
      )

      val moduleClass = ModuleClassFactory.build(module)

      val output = moduleClass.javadoc

      assertResult (expected) { output }
    }

    it ("should mark the class as deprecated, if the deprecated key has a value") {

      val expected =
        """/**
          | * Lorem ipsum dolor sit amet
          | * <p>
          | * @deprecated use other_module instead
          | */""".stripMargin

      val module = ModuleMetadata(
        "AModule",
        "a_module",
        "test",
        Some("Lorem ipsum dolor sit amet"),
        None,
        List(),
        List(),
        List(),
        None,
        List(),
        Some("use other_module instead")
      )

      val moduleClass = ModuleClassFactory.build(module)

      val output = moduleClass.javadoc

      assertResult (expected) { output }
    }

    it ("should mark an option as deprecated if the description contains the word 'depreacted'") {

      val expected = List(
        """/**
          | * Lorem ipsum dolor sit amet
          | */""",
        """    /**
          |     * Curabitur vel blandit elit. It's DEPRECATED, use another_option instead.
          |     * <p>
          |     * @deprecated
          |     */"""
      ).map(_.stripMargin)

      val module = ModuleMetadata(
        "AModule",
        "a_module",
        "test",
        Some("Lorem ipsum dolor sit amet"),
        None,
        List(
          OptionMetadata(
            "anOption",
            "an_option",
            true,
            Some("Curabitur vel blandit elit. It's DEPRECATED, use another_option instead."),
            None,
            None,
            List())),
        List(),
        List(),
        None,
        List(),
        None
      )

      val moduleClass = ModuleClassFactory.build(module)

      assertResult (expected) { List(moduleClass.javadoc, moduleClass.setters.head.javadoc) }
    }
  }
}


