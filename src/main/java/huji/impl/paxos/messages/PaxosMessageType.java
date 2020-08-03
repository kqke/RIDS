package huji.impl.paxos.messages;

public enum PaxosMessageType {
    OFFER,
    ACK_OFFER,
    ACK_OFFER_LOCKED,
    LOCK,
    ACK_LOCK,
    DONE,
    VOTE,
    VC_STATE,
    VC_STATE_LOCK,
    VC_STATE_DONE,
    HISTORY_REQ,
    HISTORY
}
