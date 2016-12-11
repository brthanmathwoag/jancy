package jancy.core;


import jancy.core.helpers.ArraysHelper;

public class Inventory {
    private final Host[] hosts;
    private final Group[] groups;
    private final String name;

    public Inventory(String name) {
        this.hosts = new Host[0];
        this.groups = new Group[0];
        this.name = name;
    }

    private Inventory(Host[] hosts, Group[] groups, String name) {
        this.hosts = hosts;
        this.groups = groups;
        this.name = name;
    }

    public Inventory hosts(Host[] hosts) {
        return new Inventory(
            ArraysHelper.copyIfNotEmpty(hosts),
            this.groups,
            name);
    }

    public Inventory groups(Group[] groups) {
        return new Inventory(
            this.hosts,
            ArraysHelper.copyIfNotEmpty(groups),
            name);
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
}
