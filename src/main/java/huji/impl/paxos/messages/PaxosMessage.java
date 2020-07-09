package huji.impl.paxos.messages;

import huji.messages.Message;

public class PaxosMessage extends Message {
    public PaxosMessage(UUID from, UUID to, String body) {
        super(from, to, body);
    }
}
