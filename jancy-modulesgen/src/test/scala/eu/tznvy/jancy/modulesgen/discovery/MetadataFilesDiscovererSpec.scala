package eu.tznvy.jancy.modulesgen.discovery

import org.scalatest.FunSpec
import scala.annotation.tailrec
import scala.io.Source

class MetadataFilesDiscovererSpec extends FunSpec {

  describe("The MetadataFilesDiscoverer") {

    it("should only consider python files") {

      val filename = "readme.md"
      val source = Source.fromString(
        """You set up the module like this:
          |
          |```python
          |module = AnsibleModule(
          | #...
          |)
          |```
          |""".stripMargin)

      val output = MetadataFilesDiscoverer.isAnsibleModuleFile(filename, source)

      assertResult (false) { output }
    }

    it("should filter out arbitrary python files") {

      val filename = "readme.md"
      val source = Source.fromString(
        """#!/usr/bin/env python
          |
          |def main():
          |    println("Hello world")
          |
          |main()
          |
          |""".stripMargin)

      val output = MetadataFilesDiscoverer.isAnsibleModuleFile(filename, source)

      assertResult (false) { output }
    }

    it("should look for AnsibleModule constructor invocation") {

      val filename = "a-test-module.py"
      val source = Source.fromString(
        """#!/usr/bin/python
          |
          |def main():
          |    module = AnsibleModule(
          |        argument_spec = dict(
          |            name = dict(required=True),
          |            arguments = dict(aliases=['args'], default=''),
          |        ),
          |        supports_check_mode=True,
          |        required_one_of=[['state', 'enabled']],
          |    )
          |```
          |""".stripMargin)

      val output = MetadataFilesDiscoverer.isAnsibleModuleFile(filename, source)

      assertResult (true) { output }
    }

    it("should look for 'a virtual module' annotation") {

      val filename = "a-test-module.py"
      val source = Source.fromString(
        """# this is a virtual module that is entirely implemented server side
          |
          |# The license goes here
          |
          |ANSIBLE_METADATA = {'status': ['stableinterface'],
          |                    'supported_by': 'core',
          |                    'version': '1.0'}
          |
          |DOCUMENTATION = '''
          |---
          |the documentation goes here
          |'''
          |
          |EXAMPLES = '''
          | - the examples go here
          |'''
          |
          |""".stripMargin)

      val output = MetadataFilesDiscoverer.isAnsibleModuleFile(filename, source)

      assertResult (true) { output }
    }
  }
}
