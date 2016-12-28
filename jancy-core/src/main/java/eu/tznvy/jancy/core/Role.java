package eu.tznvy.jancy.core;


import eu.tznvy.jancy.core.helpers.ArraysHelper;

import java.util.HashMap;
import java.util.Map;

public class Role {
    private final Handler[] handlers;
    private final String name;
    private final Task[] tasks;
    private final Map<String, Object> vars;

    public Role(String name) {
        this.handlers = new Handler[0];
        this.name = name;
        this.tasks = new Task[0];
        this.vars = new HashMap<>();
    }

    private Role(Handler[] handlers, String name, Task[] tasks, Map<String, Object> vars) {
        this.handlers = ArraysHelper.copyIfNotEmpty(handlers);
        this.name = name;
        this.tasks = tasks;
        this.vars = new HashMap<>(vars);
    }

    public Role handlers(Handler... handlers) {
        return new Role(
            ArraysHelper.copyIfNotEmpty(handlers),
            this.name,
            this.tasks,
            this.vars);
    }

    public Role tasks(Task... tasks) {
        return new Role(
            this.handlers,
            this.name,
            ArraysHelper.copyIfNotEmpty(tasks),
            this.vars);
    }

    public Role vars(Map<String, Object> vars) {
        return new Role(
            this.handlers,
            this.name,
            this.tasks,
            new HashMap<>(vars));
    }

    public Handler[] getHandlers() {
        return ArraysHelper.copyIfNotEmpty(this.handlers);
    }

    public String getName() {
        return this.name;
    }

    public Task[] getTasks() {
        return ArraysHelper.copyIfNotEmpty(tasks);
    }

    public Map<String, Object> getVars() {
        return new HashMap<>(this.vars);
    }
}
