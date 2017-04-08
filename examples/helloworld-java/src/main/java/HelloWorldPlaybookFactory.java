import eu.tznvy.jancy.core.Playbook;
import eu.tznvy.jancy.core.PlaybookFactory;
import eu.tznvy.jancy.core.Host;
import eu.tznvy.jancy.core.Play;
import eu.tznvy.jancy.core.Task;
import eu.tznvy.jancy.core.Inventory;
import eu.tznvy.jancy.modules.files.Copy;
import java.util.HashMap;

public class HelloWorldPlaybookFactory implements PlaybookFactory {
    @Override
    public Playbook build() {
        Host vm = new Host("vm")
            .vars(
                new HashMap<String, Object>() {{
                    put("ansible_host", "127.0.0.1");
                    put("ansible_port", "2222");
                }}
        );

        Inventory inventory = new Inventory("inventory")
            .hosts(vm);

        Task motdTask = new Copy()
            .dest("/etc/motd")
            .content("Hello world!\n")
            .toTask("Set up motd");

        Play play = new Play("")
            .hosts(vm)
            .tasks(motdTask);

        return new Playbook("helloworld")
            .inventories(inventory)
            .plays(play);
    }
}
