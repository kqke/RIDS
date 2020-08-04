package huji.impl.paxos.messages;

import huji.impl.ViewChangeAble.messages.ViewAbleMessage;
import huji.impl.ViewChangeAble.messages.ViewAbleType;

public class PaxosMessage extends ViewAbleMessage<PaxosValue> {
    public final PaxosMessageType ptype;

    public PaxosMessage(int from, int to, PaxosValue body, boolean isClient, int view, int storage, ViewAbleType view_type, PaxosMessageType type) {
        super(from, to, body, isClient, view, storage, view_type);
        this.ptype = type;
    }

    public PaxosMessage(int from, int to, PaxosValue body, boolean isClient, int view, int storage, PaxosMessageType type) {
        this(from, to, body, isClient, view, storage, ViewAbleType.OTHER, type);
    }

    public PaxosMessage(int from, int to, PaxosValue body, int view, int storage, PaxosMessageType type) {
        this(from, to, body, false, view, storage, ViewAbleType.OTHER, type);
    }

    public PaxosMessage(int from, PaxosValue body, int view, int storage, PaxosMessageType type) {
        this(from, -1, body, false, view, storage, ViewAbleType.OTHER, type);
    }
}
