package huji.logger.logs;

import java.util.Map;

public class Log {
    Map<String,Object> parameters;

    public Log() {}

    public void addParameter( String key, Object value ) {
        parameters.putIfAbsent(key, value);
    }

    @Override
    public String toString() {
        return parameters.toString();
    }
}
