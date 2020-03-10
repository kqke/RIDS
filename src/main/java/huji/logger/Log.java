package huji.logger;

import java.util.HashMap;
import java.util.Map;

public class Log {
    private Map<String,Object> parameters;

    public Log() {
        parameters = new HashMap<>();
    }

    public Log parameter( String key, Object value ) {
        parameters.putIfAbsent(key, value);
        return this;
    }

    @Override
    public String toString() {
        return parameters.toString();
    }
}
