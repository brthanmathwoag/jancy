package eu.tznvy.jancy.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Task extends Tasklike<Task> {

    public Task(String name) {
        super(name);
    }

    private Task(Map<String, Object> arguments, Optional<Action> action) {
        super(arguments, action);
    }

    @Override
    protected Task build(Map<String, Object> arguments, Optional<Action> action) {
        return new Task(arguments, action);
    }
}

