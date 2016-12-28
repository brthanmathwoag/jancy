package eu.tznvy.jancy.core;


import eu.tznvy.jancy.core.helpers.ArraysHelper;

import java.util.HashMap;
import java.util.Map;

public class Group implements Hosts {
    private final String name;
    private final Host[] hosts;
    private final Map<String, Object> vars;

    public Group(String name) {
        this.name = name;
        hosts = new Host[0];
        vars = new HashMap<>();
    }

    private Group(String name, Host[] hosts, Map<String, Object> vars) {
        this.name = name;
        this.hosts = hosts;
        this.vars = vars;
    }

    public Group hosts(Host... hosts) {
        return new Group(
            this.name,
            ArraysHelper.copyIfNotEmpty(hosts),
            this.vars);
    }

    public Group vars(Map<String, Object> vars) {
        return new Group(
            this.name,
            this.hosts,
            new HashMap<>(vars));
    }

    public String getName() {
        return this.name;
    }

    public Host[] getHosts() {
        return ArraysHelper.copyIfNotEmpty(hosts);
    }

    public Map<String, Object> getVars() {
        return new HashMap<>(this.vars);
    }
}

