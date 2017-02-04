package eu.tznvy.jancy.core;


import java.util.HashMap;
import java.util.Map;

public class Inventory {
    private final Host[] hosts;
    private final Group[] groups;
    private final String name;
    private final Map<String, Object> vars;

    public Inventory(String name) {
        this.hosts = new Host[0];
        this.groups = new Group[0];
        this.name = name;
        this.vars = new HashMap<>();
    }

    private Inventory(Host[] hosts, Group[] groups, String name, Map<String, Object> vars) {
        this.hosts = hosts;
        this.groups = groups;
        this.name = name;
        this.vars = vars;
    }

    public Inventory hosts(Host... hosts) {
        return new Inventory(
            ArraysHelper.copyIfNotEmpty(hosts),
            this.groups,
            this.name,
            this.vars);
    }

    public Inventory groups(Group... groups) {
        return new Inventory(
            this.hosts,
            ArraysHelper.copyIfNotEmpty(groups),
            this.name,
            this.vars);
    }

    public Inventory vars(Map<String, Object> vars) {
        return new Inventory(
            this.hosts,
            this.groups,
            this.name,
            new HashMap<>(vars));
    }

    public Group[] getGroups() {
        return ArraysHelper.copyIfNotEmpty(groups);
    }

    public Host[] getHosts() {
        return ArraysHelper.copyIfNotEmpty(hosts);
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Object> getVars() {
        return new HashMap<>(this.vars);
    }
}
