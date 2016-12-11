package jancy.core;


import jancy.core.helpers.ArraysHelper;

public class Group implements Hosts {
    private final String name;
    private final Host[] hosts;
    //TODO: recursive groups
    //TODO: groupvars

    public Group(String name) {
        this.name = name;
        hosts = new Host[0];
    }

    private Group(String name, Host[] hosts) {
        this.name = name;
        this.hosts = hosts;
    }

    public Group hosts(Host[] hosts) {
        return new Group(
            name,
            ArraysHelper.copyIfNotEmpty(hosts));
    }

    public String getName() {
        return this.name;
    }

    public Host[] getHosts() {
        return ArraysHelper.copyIfNotEmpty(hosts);
    }
}

