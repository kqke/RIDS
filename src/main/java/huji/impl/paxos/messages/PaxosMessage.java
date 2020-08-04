package huji.impl.paxos.messages;

import huji.impl.ViewChangeAble.messages.ViewAbleMessage;
import huji.impl.ViewChangeAble.messages.ViewAbleType;

import java.util.Map;

public class PaxosMessage extends ViewAbleMessage<PaxosValue> {
    public final PaxosMessageType ptype;

    public PaxosMessage(int from, int to, PaxosValue body, boolean isClient, int view, int storage, ViewAbleType type, PaxosMessageType ptype) {
        super(from, to, body, isClient, view, storage, type);
        this.ptype = ptype;
    }

    public PaxosMessage(int from, int to, PaxosValue body, boolean isClient, int view, int storage, PaxosMessageType ptype) {
        this(from, to, body, isClient, view, storage, ViewAbleType.OTHER, ptype);
    }

    public PaxosMessage(int from, int to, PaxosValue body, int view, int storage, PaxosMessageType ptype) {
        this(from, to, body, false, view, storage, ViewAbleType.OTHER, ptype);
    }

    public PaxosMessage(int from, PaxosValue body, int view, int storage, PaxosMessageType ptype) {
        this(from, -1, body, false, view, storage, ViewAbleType.OTHER, ptype);
    }

    public PaxosMessage(PaxosMessage other, int to) {
        this(other.from, to, other.body, other.isClient, other.view, other.storage, other.type, other.ptype);
    }

    @Override
    public PaxosMessage copy(int to) {
        return new PaxosMessage(this, to);
    }

    @Override
    public PaxosMessage add_property(String key, Object value) {
        super.add_property(key, value);
        return this;
    }
}
