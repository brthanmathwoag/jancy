import eu.tznvy.jancy.core.Playbook
import eu.tznvy.jancy.core.PlaybookFactory
import eu.tznvy.jancy.core.Host
import eu.tznvy.jancy.core.Play
import eu.tznvy.jancy.core.Task
import eu.tznvy.jancy.core.Inventory
import eu.tznvy.jancy.modules.files.Copy

class HelloWorldPlaybookFactory : PlaybookFactory {
    override fun build(): Playbook {
        val vm = Host("vm")
            .vars(
                mapOf(
                    "ansible_host" to "127.0.0.1",
                    "ansible_port" to "2222"
                )
            )

        val inventory = Inventory("inventory")
            .hosts(vm)

        val motdTask = Copy()
            .dest("/etc/motd")
            .content("Hello world!\n")
            .toTask("Set up motd")

        val play = Play("")
            .hosts(vm)
            .tasks(motdTask)

        return Playbook("helloworld")
            .inventories(inventory)
            .plays(play)
    }
}
