package jancy.core;


import jancy.core.Configuration;

public interface ConfigurationFactory {
    Configuration build();
    String getName();
}
