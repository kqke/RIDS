package huji.messages.impl;

import huji.messages.Message;
import huji.messages.Type;

public class AckMessage extends Message {

    public AckMessage(int view, int from, int to) {
        super(Type.ACK, view, from, to);
    }
}
