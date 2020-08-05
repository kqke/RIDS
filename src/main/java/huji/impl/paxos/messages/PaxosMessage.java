package huji.impl.paxos.messages;

import huji.impl.viewChangeAble.messages.ViewAbleMessage;
import huji.impl.viewChangeAble.messages.ViewAbleType;
import huji.impl.paxos.resources.PaxosVCState;

public class PaxosMessage<T extends Comparable<T>> extends ViewAbleMessage<T> {
    public final PaxosMessageType ptype;

    public PaxosMessage(int from, int to, T body, boolean isClient, int view, int storage, ViewAbleType type, PaxosMessageType ptype) {
        super(from, to, body, isClient, view, storage, type);
        this.ptype = ptype;
    }

    public PaxosMessage(int from, int to, T body, boolean isClient, int view, int storage, PaxosMessageType ptype) {
        this(from, to, body, isClient, view, storage, ViewAbleType.OTHER, ptype);
    }

    public PaxosMessage(int from, int to, T body, int view, int storage, PaxosMessageType ptype) {
        this(from, to, body, false, view, storage, ViewAbleType.OTHER, ptype);
    }

    public PaxosMessage(int from, T body, int view, int storage, PaxosMessageType ptype) {
        this(from, -1, body, false, view, storage, ViewAbleType.OTHER, ptype);
    }

    public PaxosMessage(PaxosMessage<T> other, int to) {
        this(other.from, to, other.body, other.isClient, other.view, other.storage, other.type, other.ptype);
    }

    @Override
    public PaxosMessage<T> copy(int to) {
        return new PaxosMessage<>(this, to);
    }

    @Override
    public PaxosMessage<T> add_property(String key, Object value) {
        super.add_property(key, value);
        return this;
    }

    public PaxosVCState get_state_property(String key) {
        return (PaxosVCState) this.properties.get(key);
    }

    @Override
    public String toString() {
        return super.toString() + ", ptype=" + ptype.toString();
    }
}
