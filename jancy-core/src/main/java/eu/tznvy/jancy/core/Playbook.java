package eu.tznvy.jancy.core;


import eu.tznvy.jancy.core.helpers.ArraysHelper;

import java.util.LinkedList;
import java.util.List;

public class Playbook {
    private final String[] hosts;
    private final String name;
    private final String[] roles;
    private final Task[] tasks;

//    private final Handler[] handlers;
//    private final Template[] templates;
//    private final File[] files;
//    private final Var[] vars;
//    private final Defaults[] defaults;
//    private final Meta[] meta;

    public Playbook(String name) {
        this.hosts = new String[0];
        this.name = name;
        this.roles = new String[0];
        this.tasks = new Task[0];
    }

    private Playbook(String[] hosts, String name, String[] roles, Task[] tasks) {
        this.hosts = hosts;
        this.name = name;
        this.roles = roles;
        this.tasks = tasks;
    }

    public Playbook hosts(Hosts... hosts) {
        List<String> names = new LinkedList<>();
        for(Hosts h : hosts) {
            names.add(h.getName());
        }

        return new Playbook(
            names.toArray(new String[names.size()]),
            this.name,
            this.roles,
            this.tasks);
    }

    public Playbook roles(Role... roles) {
        List<String> names = new LinkedList<>();
        for(Role r : roles) {
            names.add(r.getName());
        }

        return new Playbook(
            this.hosts,
            this.name,
            names.toArray(new String[names.size()]),
            this.tasks);
    }

    public Playbook tasks(Task... tasks) {
        return new Playbook(
            this.hosts,
            this.name,
            this.roles,
            ArraysHelper.copyIfNotEmpty(tasks));
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
