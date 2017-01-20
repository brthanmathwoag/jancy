package eu.tznvy.jancy.core;


import eu.tznvy.jancy.core.helpers.ArraysHelper;

import java.util.LinkedList;
import java.util.List;

public class Play {
    private final Handler[] handlers;
    private final String[] hosts;
    private final String name;
    private final String[] roles;
    private final Task[] tasks;

    public Play(String name) {
        this.handlers = new Handler[0];
        this.hosts = new String[0];
        this.name = name;
        this.roles = new String[0];
        this.tasks = new Task[0];
    }

    private Play(Handler[] handlers, String[] hosts, String name, String[] roles, Task[] tasks) {
        this.handlers = handlers;
        this.hosts = hosts;
        this.name = name;
        this.roles = roles;
        this.tasks = tasks;
    }

    public Play handlers(Handler... handlers) {
        return new Play(
            ArraysHelper.copyIfNotEmpty(handlers),
            this.hosts,
            this.name,
            this.roles,
            this.tasks);
    }

    public Play hosts(Hosts... hosts) {
        List<String> names = new LinkedList<>();
        for(Hosts h : hosts) {
            names.add(h.getName());
        }

        return new Play(
            this.handlers,
            names.toArray(new String[names.size()]),
            this.name,
            this.roles,
            this.tasks);
    }

    public Play roles(Role... roles) {
        List<String> names = new LinkedList<>();
        for(Role r : roles) {
            names.add(r.getName());
        }

        return new Play(
            this.handlers,
            this.hosts,
            this.name,
            names.toArray(new String[names.size()]),
            this.tasks);
    }

    public Play tasks(Task... tasks) {
        return new Play(
            this.handlers,
            this.hosts,
            this.name,
            this.roles,
            ArraysHelper.copyIfNotEmpty(tasks));
    }

    public Handler[] getHandlers() {
        return ArraysHelper.copyIfNotEmpty(this.handlers);
    }

    public String[] getHosts() {
        return this.hosts;
    }

    public String getName() {
        return this.name;
    }

    public String[] getRoles() {
        return this.roles;
    }

    public Task[] getTasks() {
        return ArraysHelper.copyIfNotEmpty(this.tasks);
    }
}
