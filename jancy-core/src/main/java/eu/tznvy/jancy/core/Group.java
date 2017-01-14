package eu.tznvy.jancy.core;


import eu.tznvy.jancy.core.helpers.ArraysHelper;

import java.util.HashMap;
import java.util.Map;

public class Group implements Hosts {
    private final String name;
    private final Host[] hosts;
    private final Group[] subgroups;
    private final Map<String, Object> vars;

    public Group(String name) {
        this.name = name;
        hosts = new Host[0];
        subgroups = new Group[0];
        vars = new HashMap<>();
    }

    private Group(String name, Host[] hosts, Group[] subgroups, Map<String, Object> vars) {
        this.name = name;
        this.hosts = hosts;
        this.subgroups = subgroups;
        this.vars = vars;
    }

    public Group hosts(Host... hosts) {
        return new Group(
            this.name,
            ArraysHelper.copyIfNotEmpty(hosts),
            this.subgroups,
            this.vars);
    }

    public Group subgroups(Group... subgroups) {
        return new Group(
            this.name,
            this.hosts,
            ArraysHelper.copyIfNotEmpty(subgroups),
            this.vars);
    }

    public Group vars(Map<String, Object> vars) {
        return new Group(
            this.name,
            this.hosts,
            this.subgroups,
            new HashMap<>(vars));
    }

    public String getName() {
        return this.name;
    }

    public Group[] getSubgroups() {
        return ArraysHelper.copyIfNotEmpty(subgroups);
    }

    public Host[] getHosts() {
        return ArraysHelper.copyIfNotEmpty(hosts);
    }

    public Map<String, Object> getVars() {
        return new HashMap<>(this.vars);
    }
}

