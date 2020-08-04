package huji.impl.paxos.messages;

public enum PaxosMessageType {
    OFFER,
    ACK_OFFER,
    LOCK,
    ACK_LOCK,
    DONE,
    VOTE,
    VC,
    OTHER
}
