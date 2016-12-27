package eu.tznvy.jancy.core;


import eu.tznvy.jancy.core.Configuration;

public interface ConfigurationFactory {
    Configuration build();
    String getName();
}
