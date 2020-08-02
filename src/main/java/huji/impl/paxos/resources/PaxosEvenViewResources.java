package huji.impl.paxos.resources;

import huji.impl.paxos.messages.PaxosValue;

import java.util.Hashtable;

public class PaxosEvenViewResources {

    // Even view counters
    private int ack_offer_counter;
    private int ack_lock_counter;
    private int done_counter;

    public PaxosEvenViewResources(int n, int f){
        ack_offer_counter = n - f;
        ack_lock_counter = n - f;
        done_counter = n - f;
    }

    public boolean countdownAckOffer() {
        return 0 == --ack_offer_counter;
    }

    public boolean countdownAckLock() {
        return 0 == --ack_lock_counter;
    }

    public boolean countdownDone() {
        return 0 == --done_counter;
    }

}
