package eu.tznvy.jancy.core;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Tasklike {
    protected final Optional<Action> action;
    protected final Map<String, String> arguments;

    protected Tasklike(String name) {
        this.arguments = new HashMap<>();
        arguments.put("name", name);
        this.action = Optional.empty();
    }

    protected Tasklike(Map<String, String> arguments, Optional<Action> action) {
        this.arguments = new HashMap<>(arguments);
        this.action = action;
    }

    protected Map<String, String> withArgument(String key, String value) {
        Map<String, String> argumentsCopy = new HashMap<>(this.arguments);
        argumentsCopy.put(key, value);
        return argumentsCopy;
    }

    public Map<String, String> getArguments() {
        return new HashMap<>(this.arguments);
    }

    public Optional<Action> getAction() {
        return this.action;
    }
}
