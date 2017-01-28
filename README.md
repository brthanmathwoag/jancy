# jancy

jancy is a framework for authoring [Ansible](https://www.ansible.com/) playbooks in Java (or any other language
targeting JVM).

[![build status](https://jancy.tznvy.eu/content/buildstatus.svg)](https://travis-ci.org/brthanmathwoag/jancy)
[![tests status](https://jancy.tznvy.eu/content/testsstatus.svg)](https://travis-ci.org/brthanmathwoag/jancy)
[![test coverage](https://jancy.tznvy.eu/content/coveragestatus.svg)](https://travis-ci.org/brthanmathwoag/jancy)

## Getting jancy

jancy is currently in development and no stable version has been released yet. Feel free to try out the current snapshot.
You can download it from [here](https://jancy.tznvy.eu/m2/eu/tznvy/jancy-transpiler_2.12/0.1.0-SNAPSHOT/jancy-transpiler_2.12-0.1.0-SNAPSHOT.jar).
Just make sure to rename the jar to `jancy` before proceeding.

To build it from source:

```bash
git clone https://github.com/brthanmathwoag/jancy
cd jancy
./sbt jancyTranspiler/assembly
cp jancy-transpiler/target/scala-2.12/jancy ./
```

Make note you don't need to have sbt (or Scala) installed beforehand; Just run the `sbt` script from
this repository and it will install the current version of sbt if it cannot be found in the path.

## Usage

```bash
$ jancy
usage: jancy -j /path/to/configuration.jar [-o /output/path/] [-c
             com.example.Class]
 -c,--class <com.example.Class>          The name of the PlaybookFactory
                                         implementation to be transpiled.
                                         If not set, all PlaybookFactories
                                         in the jar will be used.
 -h,--help                               Prints usage information and
                                         exits.
 -j,--jar </path/to/configuration.jar>   The path to a jar file containing
                                         the playbooks.
 -o,--output </output/path/>             The directory where the ansible
                                         configuration will be saved.
                                         Defaults to current directory.
 -v,--version                            Prints version information and
                                         exits.
```

## Getting started

Add `jancy-common` as a dependency. If you use Maven:

```xml
<project>
    <repositories>
        <repository>
            <id>jancy-snapshots</id>
            <name>jancy snapshots</name>
            <url>https://jancy.tznvy.eu/m2</url>
        </repository>
    </repositories>

    <dependencies>
        <dependency>
            <groupId>eu.tznvy</groupId>
            <artifactId>jancy-common</artifactId>
            <version>0.1.0-SNAPSHOT</version>
        </dependency>
    </dependencies>
</project>
```

If you use sbt:

```scala
resolvers += "jancy snapshots" at "https://jancy.tznvy.eu/m2"
libraryDependencies += "eu.tznvy" % "jancy-common" % "0.1.0-SNAPSHOT"
```

Create a class implementing the `PlaybookFactory` interface from `jancy-common-*.jar`:

```java
import eu.tznvy.jancy.core.*;
import eu.tznvy.jancy.modules.system.Ping;

public class HelloWorldPlaybookFactory implements PlaybookFactory {
    @Override
    public Playbook build() {
        Host localhost = new Host("localhost");

        return new Playbook("HelloWorld")
            .inventories(
                new Inventory("inventory")
                    .hosts(localhost))
            .plays(
                new Play("Preflight check")
                    .hosts(localhost)
                    .tasks(
                        new Task("Test connection")
                            .action(new Ping())));
    }
}
```

Package your class in a jar with `jancy-common-*.jar` and other dependencies, then run `jancy` against it:

```bash
./jancy --jar yourjar.jar --output ./
```

jancy will look for PlaybookFactories in your jar and create a directory with Ansible configuration for each
one.

```bash
cd HelloWorld
ansible-playbook -i inventory site.yml
```

For an overview of available features, see [Configuration Concepts page](https://jancy.tznvy.eu/concepts).

## Examples

Browse the examples [here](https://github.com/brthanmathwoag/jancy/tree/master/examples)

```bash
# make sure the transpiler is built
$ ./sbt jancyTranspiler/assembly

# assemble jars for example playbooks
$ ./sbt examples/assembly

# generate playbook from the jar
$ jancy-transpiler/target/scala-2.12/jancy \
    --jar examples/lamp_simple/target/lampSimpleExample*.jar \
    --output /tmp/

$ cd /tmp/lamp_simple/
$ tree
.
└── lamp_simple
    ├── group_vars
    │   ├── all
    │   └── dbservers
    ├── hosts
    ├── roles
    │   ├── common
    │   │   ├── handlers
    │   │   │   └── main.yml
    │   │   ├── tasks
    │   │   │   └── main.yml
    │   │   └── templates
    │   │       └── ntp.conf.j2
    │   ├── db
    │   │   ├── handlers
    │   │   │   └── main.yml
    │   │   ├── tasks
    │   │   │   └── main.yml
    │   │   └── templates
    │   │       └── my.cnf.j2
    │   └── web
    │       ├── handlers
    │       │   └── main.yml
    │       ├── tasks
    │       │   └── main.yml
    │       └── templates
    │           └── index.php.j2
    └── site.yml
15 directories, 13 files

# run the playbook
$ ansible-playbook -i hosts site.yml
```
