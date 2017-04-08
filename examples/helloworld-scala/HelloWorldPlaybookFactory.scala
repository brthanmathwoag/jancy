import scala.collection.JavaConverters._
import eu.tznvy.jancy.core.{Playbook, PlaybookFactory, Host, Play, Task, Inventory}
import eu.tznvy.jancy.modules.files.Copy

class HelloWorldPlaybookFactory extends PlaybookFactory {
  override def build(): Playbook = {
    val vm = new Host("vm")
      .vars(
        Map[String, Object](
          "ansible_ssh_host" -> "127.0.0.1",
          "ansible_ssh_port" -> "2222"
        ).asJava
      )
      
    val inventory = new Inventory("inventory")
      .hosts(vm)
      
    val motdTask = new Copy()
      .dest("/etc/motd")
      .content("Hello world!\n")
      .toTask("Set up motd")
      
    val play = new Play("")
      .hosts(vm)
      .tasks(motdTask)
      
    new Playbook("helloworld")
      .inventories(inventory)
      .plays(play)
  }
}
