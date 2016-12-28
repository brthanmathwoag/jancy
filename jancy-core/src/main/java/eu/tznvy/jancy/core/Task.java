package eu.tznvy.jancy.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Task extends Tasklike {

    public Task(String name) {
        super(name);
    }

    private Task(Map<String, Object> arguments, Optional<Action> action) {
        super(arguments, action);
    }

    public Task action(Action action) {
        return new Task(this.arguments, Optional.of(action));
    }

    public Task notify(Object... value) {
        return new Task(withArgument("notify", value), this.action);
    }

    public Task when(String value) {
        return new Task(withArgument("when", value), this.action);
    }

    public Task withItems(Object... values) {
        return new Task(withArgument("with_items", values), this.action);
    }

    public Task withNested(Object... value) {
        return new Task(withArgument("with_nested", value), this.action);
    }

    public Task withDict(Object... value) {
        return new Task(withArgument("with_dict", value), this.action);
    }

    public Task withFile(Object... value) {
        return new Task(withArgument("with_file", value), this.action);
    }

    public Task withFileglob(Object... value) {
        return new Task(withArgument("with_fileglob", value), this.action);
    }

    public Task withTogether(Object... value) {
        return new Task(withArgument("with_together", value), this.action);
    }

    public Task withSubelements(Object... value) {
        return new Task(withArgument("with_subelements", value), this.action);
    }

    public Task withSequence(String value) {
        return new Task(withArgument("with_sequence", value), this.action);
    }

    public Task withRandomChoice(Object... value) {
        return new Task(withArgument("with_random_choice", value), this.action);
    }

    public Task register(String value) {
        return new Task(withArgument("register", value), this.action);
    }

    public Task retries(String value) {
        return new Task(withArgument("retries", value), this.action);
    }

    public Task delay(String value) {
        return new Task(withArgument("delay", value), this.action);
    }

    public Task withFirstFound(Object... value) {
        return new Task(withArgument("with_first_found", value), this.action);
    }

    public Task withLines(String value) {
        return new Task(withArgument("with_lines", value), this.action);
    }

    public Task withIndexedItems(Object... value) {
        return new Task(withArgument("with_indexed_items", value), this.action);
    }

    public Task withIni(String value) {
        return new Task(withArgument("with_ini", value), this.action);
    }

    public Task withFlattened(Object... value) {
        return new Task(withArgument("with_flattened", value), this.action);
    }

    public Task withInventoryHostnames(Object... value) {
        return new Task(withArgument("with_inventory_hostnames", value), this.action);
    }

    public Task meta(String value) {
        return new Task(withArgument("meta", value), this.action);
    }
}
