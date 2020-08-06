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

    public Message<T> addProperty(String key, Object value) {
        this.properties.put(key, value);
        return this;
    }

    public Object getProperty(String key) { return this.properties.get(key); }
    public Map<Integer,T> getMapProperty(String key) { return (Map<Integer,T>) getProperty(key); }
    public String getStringProperty(String key) { return (String) getProperty(key); }
    public int getIntProperty(String key) { return (Integer) getProperty(key); }
    public boolean getBoolProperty(String key) { return (Boolean) getProperty(key); }

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
