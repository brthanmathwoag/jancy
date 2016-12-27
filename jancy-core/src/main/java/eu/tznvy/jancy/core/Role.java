package eu.tznvy.jancy.core;


import eu.tznvy.jancy.core.helpers.ArraysHelper;

public class Role {
    private final Handler[] handlers;
    private final String name;
    private final Task[] tasks;

    public Role(String name) {
        this.handlers = new Handler[0];
        this.name = name;
        this.tasks = new Task[0];
    }

    private Role(Handler[] handlers, String name, Task[] tasks) {
        this.handlers = ArraysHelper.copyIfNotEmpty(handlers);
        this.name = name;
        this.tasks = tasks;
    }

    public Role handlers(Handler... handlers) {
        return new Role(
            ArraysHelper.copyIfNotEmpty(handlers),
            this.name,
            this.tasks);
    }

    public Role tasks(Task... tasks) {
        return new Role(
            this.handlers,
            this.name,
            ArraysHelper.copyIfNotEmpty(tasks));
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
}
