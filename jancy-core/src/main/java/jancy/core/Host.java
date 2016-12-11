package jancy.core;


public class Host implements Hosts {
    private final String name;
    //TODO: hostvars

    public Host(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
