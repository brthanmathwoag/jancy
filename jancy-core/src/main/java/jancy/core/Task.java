package jancy.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Task {
    private final Optional<Action> action;
    private final Map<String, String> arguments;

    public Task(String name) {
        this.arguments = new HashMap<>();
        arguments.put("name", name);
        this.action = Optional.empty();
    }

    private Task(Map<String, String> arguments, Optional<Action> action) {
        this.arguments = new HashMap<>(arguments);
        this.action = action;
    }

    private Map<String, String> withArgument(String key, String value) {
        Map<String, String> argumentsCopy = new HashMap<>(this.arguments);
        argumentsCopy.put(key, value);
        return argumentsCopy;
    }

    public Task action(Action action) {
        return new Task(this.arguments, Optional.of(action));
    }

    public Map<String, String> getArguments() {
        return new HashMap<>(this.arguments);
    }

    public Optional<Action> getAction() {
        return this.action;
    }

    public Task notify(String value) {
        return new Task(withArgument("notify", value), this.action);
    }

    public Task when(String value) {
        return new Task(withArgument("when", value), this.action);
    }

    public Task withItems(String value) {
        return new Task(withArgument("with_items", value), this.action);
    }

    public Task withNested(String value) {
        return new Task(withArgument("with_nested", value), this.action);
    }

    public Task withDict(String value) {
        return new Task(withArgument("with_dict", value), this.action);
    }

    public Task withFile(String value) {
        return new Task(withArgument("with_file", value), this.action);
    }

    public Task withFileglob(String value) {
        return new Task(withArgument("with_fileglob", value), this.action);
    }

    public Task withTogether(String value) {
        return new Task(withArgument("with_together", value), this.action);
    }

    public Task withSubelements(String value) {
        return new Task(withArgument("with_subelements", value), this.action);
    }

    public Task withSequence(String value) {
        return new Task(withArgument("with_sequence", value), this.action);
    }

    public Task withRandomChoice(String value) {
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

    public Task withFirstFound(String value) {
        return new Task(withArgument("with_first_found", value), this.action);
    }

    public Task withLines(String value) {
        return new Task(withArgument("with_lines", value), this.action);
    }

    public Task withIndexedItems(String value) {
        return new Task(withArgument("with_indexed_items", value), this.action);
    }

    public Task withIni(String value) {
        return new Task(withArgument("with_ini", value), this.action);
    }

    public Task withFlattened(String value) {
        return new Task(withArgument("with_flattened", value), this.action);
    }

    public Task withInventoryHostnames(String value) {
        return new Task(withArgument("with_inventory_hostnames", value), this.action);
    }

    public Task meta(String value) {
        return new Task(withArgument("meta", value), this.action);
    }
}
