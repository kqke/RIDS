package huji.messages.impl;

import huji.messages.Message;
import huji.messages.Type;

public class ViewChangeMessage extends Message {

    public ViewChangeMessage(int view, int from, int to) {
        super(Type.VC, view, from, to);
    }
}
