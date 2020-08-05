package huji.message;

import java.util.HashMap;
import java.util.Map;

public class Message<T> extends Delayable {
    final public int from;
    final public int to;

    final public boolean isClient;

    final public T body;
    final public Map<String,Object> properties;

    public Message(int from, int to, T body, boolean isClient) {
        super();
        this.from = from;
        this.to = to;

        this.body = body;
        this.properties = new HashMap<>();

        this.isClient = isClient;
    }

    public Message(Message<T> other, int to) {
        this(
                other.from,
                to,
                other.body,
                other.isClient
        );
        this.properties.putAll(other.properties);
    }

    public Message<T> copy(int to) {
        return new Message<>(this,to);
    }

    public Message<T> add_property(String key, Object value) {
        this.properties.putIfAbsent(key, value);
        return this;
    }

    public Object get_property(String key) {
        return this.properties.get(key);
    }

    public Map<Integer,T> get_map_property(String key) {
        return (Map<Integer,T>) this.properties.get(key);
    }

    public String get_string_property(String key) {
        return (String) this.properties.get(key);
    }

    public int get_int_property(String key) {
        return (Integer) this.properties.get(key);
    }

    public boolean get_bool_property(String key) {
        return (Boolean) this.properties.get(key);
    }

    @Override
    public String toString() {
        return ((isClient) ? "UserMessage" : "Message")
                + "{ "
                + ("from: " + from)
                + ", "
                + ("to: " + to)
                + ", "
                + ("body: " + ((body != null) ? body.toString() : "null"))
                + ", "
                + ("properties: " + ((properties.size() > 0) ? properties.toString() : "null"))
                + " }";
    }
}
