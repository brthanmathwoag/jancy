package jancy.core;


import jancy.core.helpers.ArraysHelper;

public class Role {
    private final String name;
    private final Task[] tasks;

    public Role(String name) {
        this.name = name;
        this.tasks = new Task[0];
    }

    private Role(String name, Task[] tasks) {
        this.name = name;
        this.tasks = tasks;
    }

    public Role tasks(Task... tasks) {
        return new Role(
            this.name,
            ArraysHelper.copyIfNotEmpty(tasks));
    }

    public String getName() {
        return this.name;
    }

    public Task[] getTasks() {
        return ArraysHelper.copyIfNotEmpty(tasks);
    }
}
