package huji.messages.impl;

import huji.messages.Message;
import huji.messages.Type;

public class ProposeMessage extends Message {

    final public String value;

    public ProposeMessage(int view, int from , int to, String value) {
        super(Type.PROPOSE, view, from, to);
        this.value = value;
    }


}
