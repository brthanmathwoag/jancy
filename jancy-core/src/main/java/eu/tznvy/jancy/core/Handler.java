package eu.tznvy.jancy.core;

import java.util.Map;
import java.util.Optional;

public class Handler extends Tasklike {

    public Handler(String name) {
        super(name);
    }

    private Handler(Map<String, Object> arguments, Optional<Action> action) {
        super(arguments, action);
    }

    public Handler action(Action action) {
        return new Handler(this.arguments, Optional.of(action));
    }
    
    public Handler when(String value) {
        return new Handler(withArgument("when", value), this.action);
    }

    public Handler withItems(Object... values) {
        return new Handler(withArgument("with_items", values), this.action);
    }

    public Handler withNested(Object... value) {
        return new Handler(withArgument("with_nested", value), this.action);
    }

    public Handler withDict(Object... value) {
        return new Handler(withArgument("with_dict", value), this.action);
    }

    public Handler withFile(Object... value) {
        return new Handler(withArgument("with_file", value), this.action);
    }

    public Handler withFileglob(Object... value) {
        return new Handler(withArgument("with_fileglob", value), this.action);
    }

    public Handler withTogether(Object... value) {
        return new Handler(withArgument("with_together", value), this.action);
    }

    public Handler withSubelements(Object... value) {
        return new Handler(withArgument("with_subelements", value), this.action);
    }

    public Handler withSequence(String value) {
        return new Handler(withArgument("with_sequence", value), this.action);
    }

    public Handler withRandomChoice(Object... value) {
        return new Handler(withArgument("with_random_choice", value), this.action);
    }

    public Handler register(String value) {
        return new Handler(withArgument("register", value), this.action);
    }

    public Handler retries(String value) {
        return new Handler(withArgument("retries", value), this.action);
    }

    public Handler delay(String value) {
        return new Handler(withArgument("delay", value), this.action);
    }

    public Handler withFirstFound(Object... value) {
        return new Handler(withArgument("with_first_found", value), this.action);
    }

    public Handler withLines(String value) {
        return new Handler(withArgument("with_lines", value), this.action);
    }

    public Handler withIndexedItems(Object... value) {
        return new Handler(withArgument("with_indexed_items", value), this.action);
    }

    public Handler withIni(String value) {
        return new Handler(withArgument("with_ini", value), this.action);
    }

    public Handler withFlattened(Object... value) {
        return new Handler(withArgument("with_flattened", value), this.action);
    }

    public Handler withInventoryHostnames(Object... value) {
        return new Handler(withArgument("with_inventory_hostnames", value), this.action);
    }

    public Handler meta(String value) {
        return new Handler(withArgument("meta", value), this.action);
    }
}
