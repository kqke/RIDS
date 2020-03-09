package huji.messages.impl;

import huji.messages.Message;
import huji.messages.Type;

public class ClientMessage extends Message {
    final public String value;

    public ClientMessage(int from, int to, String value) {
        super(Type.CLIENT, -1, from, to);
        this.value = value;
    }
}
