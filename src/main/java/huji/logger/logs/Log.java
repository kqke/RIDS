package huji.logger.logs;

import java.util.HashMap;
import java.util.Map;

public class Log {
    private Map<String,Object> parameters;
    final public Type type;

    public Log(Type type) {
        parameters = new HashMap<>();
        this.type = type;
    }

    public Log parameter( String key, Object value ) {
        parameters.putIfAbsent(key, value);
        return this;
    }

    @Override
    public String toString() {
        return type.toString() + " " + parameters.toString();
    }
}
