package eu.tznvy.jancy.core;


import java.util.HashMap;
import java.util.Map;

public abstract class Action {
    private final Map<String, String> arguments;

    protected Action() {
        this.arguments = new HashMap<>();
    }

    protected Action(Map<String, String> arguments) {
        this.arguments = new HashMap<>(arguments);
    }

    public abstract String getModuleName();

    protected Map<String, String> withArgument(String key, String value) {
        Map<String, String> argumentsCopy = new HashMap<>(this.arguments);
        argumentsCopy.put(key, value);
        return argumentsCopy;
    }

    public Map<String, String> getArguments() {
        return new HashMap<>(this.arguments);
    }

    public Task toTask(String name) {
        return new Task(name).action(this);
    }
}
