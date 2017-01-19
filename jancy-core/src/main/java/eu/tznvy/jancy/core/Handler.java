package eu.tznvy.jancy.core;

import java.util.Map;
import java.util.Optional;

public class Handler extends Tasklike<Handler> {

    public Handler(String name) {
        super(name);
    }

    private Handler(Map<String, Object> arguments, Optional<Action> action) {
        super(arguments, action);
    }

    @Override
    protected Handler build(Map<String, Object> arguments, Optional<Action> action) {
        return new Handler(arguments, action);
    }
}
