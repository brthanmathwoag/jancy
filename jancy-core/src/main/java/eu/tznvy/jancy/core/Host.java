package eu.tznvy.jancy.core;


import java.util.HashMap;
import java.util.Map;

public class Host implements Hosts {
    private final String name;
    private final Map<String, Object> vars;

    public Host(String name) {
        this.name = name;
        this.vars = new HashMap<>();
    }

    private Host(String name, Map<String, Object> vars) {
        this.name = name;
        this.vars = vars;
    }

    public Host vars(Map<String, Object> vars) {
        return new Host(
            this.name,
            new HashMap<>(vars));
    }

    public String getName() {
        return this.name;
    }

    public Map<String, Object> getVars() {
        return new HashMap<>(this.vars);
    }
}
