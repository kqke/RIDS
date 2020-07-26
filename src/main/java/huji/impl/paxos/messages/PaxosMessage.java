package huji.impl.paxos.messages;

import huji.message.Message;

public class PaxosMessage extends Message<PaxosValue> {
    public final int storage;
    public final int view;
    public final PaxosMessageType type;

    public PaxosMessage(int from, int to, PaxosValue body, int view, int storage, PaxosMessageType type) {
        super(from, to, body, false);
        this.view = view;
        this.storage = storage;
        this.type = type;
    }

    public PaxosMessage(PaxosMessage other, int to) {
        super(other, to);
        this.view = other.view;
        this.storage = other.storage;
        this.type = other.type;
    }

    @Override
    public Message<PaxosValue> copy(int to) {
        return new PaxosMessage(this, to);
    }
}
