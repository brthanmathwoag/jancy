package jancy.core;

import java.util.HashMap;
import java.util.Map;

public abstract class Task {
    protected final State state;

    protected Task(State state) {
        this.state = state;
    }

    protected Task(String name) {
        state = new State()
            .withModifier("name", name);
    }

    public State withActionArgument(String key, String value) {
        return state.withActionArgument(key, value);
    }

    public State withModifier(String key, String value) {
        return state.withActionArgument(key, value);
    }

    public abstract String getModuleName();

    public Map<String, String> getActionArguments() {
        return state.getActionArguments();
    }

    public Map<String, String> getModifiers() {
        return state.getModifiers();
    }

    protected class State {
        private final Map<String, String> actionArguments;
        private final Map<String, String> modifiers;

        public State() {
            actionArguments = new HashMap<>();
            modifiers = new HashMap<>();
        }

        private State(Map<String, String> actionArguments, Map<String, String> modifiers) {
            this.actionArguments = actionArguments;
            this.modifiers = modifiers;
        }

        public State withActionArgument(String key, String value) {
            Map<String, String> actionArgumentsCopy = new HashMap<>(actionArguments);
            actionArgumentsCopy.put(key, value);
            return new State(actionArgumentsCopy, modifiers);
        }

        public State withModifier(String key, String value) {
            Map<String, String> modifiersCopy = new HashMap<>(modifiers);
            modifiersCopy.put(key, value);
            return new State(actionArguments, modifiersCopy);
        }

        public Map<String, String> getActionArguments() {
            return new HashMap<>(actionArguments);
        }

        public Map<String, String> getModifiers() {
            return new HashMap<>(modifiers);
        }
    }
}
