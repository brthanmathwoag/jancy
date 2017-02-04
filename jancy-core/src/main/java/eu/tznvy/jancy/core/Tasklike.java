package eu.tznvy.jancy.core;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public abstract class Tasklike<T extends Tasklike> {
    protected final Optional<Action> action;
    protected final Map<String, Object> arguments;

    protected Tasklike(String name) {
        this.arguments = new HashMap<>();
        arguments.put("name", name);
        this.action = Optional.empty();
    }

    protected Tasklike(Map<String, Object> arguments, Optional<Action> action) {
        this.arguments = new HashMap<>(arguments);
        this.action = action;
    }

    protected abstract T build(Map<String, Object> arguments, Optional<Action> action);

    protected Map<String, Object> withArgument(String key, Object value) {
        Map<String, Object> argumentsCopy = new HashMap<>(this.arguments);
        argumentsCopy.put(key, value);
        return argumentsCopy;
    }

    public Map<String, Object> getArguments() {
        return new HashMap<>(this.arguments);
    }

    public Optional<Action> getAction() {
        return this.action;
    }

    public String getName() {
        return (String)this.arguments.get("name");
    }

    public T action(Action action) {
        return build(this.arguments, Optional.of(action));
    }

    public T notify(Handler... handlers) {
        String[] handlerNames = new String[handlers.length];
        for(int i = 0; i < handlers.length; i++) {
            handlerNames[i] = handlers[i].getName();
        }
        return build(withArgument("notify", handlerNames), this.action);
    }

    public T notify(Object... value) {
        return build(withArgument("notify", value), this.action);
    }

    public T when(String value) {
        return build(withArgument("when", value), this.action);
    }

    public T withItems(Object... values) {
        return build(withArgument("with_items", values), this.action);
    }

    public T withNested(Object... value) {
        return build(withArgument("with_nested", value), this.action);
    }

    public T withDict(Object... value) {
        return build(withArgument("with_dict", value), this.action);
    }

    public T withFile(Object... value) {
        return build(withArgument("with_file", value), this.action);
    }

    public T withFileglob(Object... value) {
        return build(withArgument("with_fileglob", value), this.action);
    }

    public T withTogether(Object... value) {
        return build(withArgument("with_together", value), this.action);
    }

    public T withSubelements(Object... value) {
        return build(withArgument("with_subelements", value), this.action);
    }

    public T withSequence(String value) {
        return build(withArgument("with_sequence", value), this.action);
    }

    public T withRandomChoice(Object... value) {
        return build(withArgument("with_random_choice", value), this.action);
    }

    public T register(String value) {
        return build(withArgument("register", value), this.action);
    }

    public T retries(String value) {
        return build(withArgument("retries", value), this.action);
    }

    public T delay(String value) {
        return build(withArgument("delay", value), this.action);
    }

    public T withFirstFound(Object... value) {
        return build(withArgument("with_first_found", value), this.action);
    }

    public T withLines(String value) {
        return build(withArgument("with_lines", value), this.action);
    }

    public T withIndexedItems(Object... value) {
        return build(withArgument("with_indexed_items", value), this.action);
    }

    public T withIni(String value) {
        return build(withArgument("with_ini", value), this.action);
    }

    public T withFlattened(Object... value) {
        return build(withArgument("with_flattened", value), this.action);
    }

    public T withInventoryHostnames(Object... value) {
        return build(withArgument("with_inventory_hostnames", value), this.action);
    }

    public T meta(String value) {
        return build(withArgument("meta", value), this.action);
    }

    public T tags(Object... value) {
        return build(withArgument("tags", value), this.action);
    }

    public T changedWhen(String value) {
        return build(withArgument("changed_when", value), this.action);
    }

    public T failedWhen(String value) {
        return build(withArgument("failed_when", value), this.action);
    }

    public T ignoreErrors(boolean value) {
        return build(withArgument("ignore_errors", value ? "yes" : "no"), this.action);
    }

    public T delegateTo(String value) {
        return build(withArgument("delegate_to", value), this.action);
    }

    public T delegateFacts(boolean value) {
        return build(withArgument("delegate_facts", value ? "yes" : "no"), this.action);
    }

    public T runOnce(boolean value) {
        return build(withArgument("run_once", value ? "yes" : "no"), this.action);
    }
}
