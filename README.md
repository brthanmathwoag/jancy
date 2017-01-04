# jancy

jancy is a framework for authoring [Ansible](https://www.ansible.com/) playbooks in Java (or any other language
targeting JVM).

[![build status](https://jancy.tznvy.eu/content/buildstatus.svg)](https://travis-ci.org/brthanmathwoag/jancy)
[![tests status](https://jancy.tznvy.eu/content/testsstatus.svg)](https://travis-ci.org/brthanmathwoag/jancy)

## Getting jancy

You can download the current development version here:

* [jancy-common-0.1.0-SNAPSHOT.jar](https://jancy.tznvy.eu/current_build/jancy-common-0.1.0-SNAPSHOT.jar)
* [jancy-common-0.1.0-SNAPSHOT-sources.jar](https://jancy.tznvy.eu/current_build/jancy-common-0.1.0-SNAPSHOT-sources.jar)
* [jancy](https://jancy.tznvy.eu/current_build/jancy)

## Usage

```bash
$ jancy
usage: jancy
 -j,--jar </path/to/configuration.jar>   The path to a jar file containing
                                         the configuration.
 -o,--output </output/path/>             The directory where the ansible
                                         configuration will be saved.
                                         Defaults to current directory.
```

## Getting started

Create a class implementing the `ConfigurationFactory` interface from `jancy-common-*.jar`:

```java
import eu.tznvy.jancy.core.*;
import eu.tznvy.jancy.modules.system.Ping;

public class HelloWorldConfigurationFactory implements ConfigurationFactory {
    @Override
    public Configuration build() {
        Host localhost = new Host("localhost");

        return new Configuration("HelloWorld")
            .inventories(
                new Inventory("inventory")
                    .hosts(localhost))
            .playbooks(
                new Playbook("Preflight check")
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

jancy will look for ConfigurationFactories in your jar and create a directory with Ansible configuration for each
one.

```bash
cd HelloWorld
ansible-playbook -i inventory site.yml
```

## Building from sources

jancy is built using sbt. You don't need to have it (or Scala) installed beforehand; Just run the `sbt` script from
 this repository and it will install the current version of sbt if it cannot be found in the path.

`./sbt test` - will run all tests

`./sbt buildAll` - will compile and assemble the jancy-common jar and the transpiler

## Examples

```bash
# make sure the transpiler is built
./sbt buildAll

# assemble jars for example playbooks
./sbt examples/assembly

# generate playbook from the jar
jancy-transpiler/target/scala-2.12/jancy \
    --jar examples/lamp_simple/target/scala-2.12/lampSimpleExample*.jar \
    --output /tmp/

# run the playbook
cd /tmp/lamp_simple/
ansible-playbook -i hosts site.yml
```
