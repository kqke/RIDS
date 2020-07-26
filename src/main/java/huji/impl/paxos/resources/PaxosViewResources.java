package huji.impl.paxos.resources;

import huji.impl.paxos.messages.PaxosValue;

public class PaxosViewResources {

    private int N;
    private int F;

    private int ack_offer_counter;
    private int ack_lock_counter;
    private int done_counter;

    private PaxosValue viewVal;

    public PaxosViewResources(int n, int f){
        N = n;
        F = f;
    }

    public void reset(PaxosValue val){
        viewVal = val;
        ack_offer_counter = N - F;
        ack_lock_counter = N - F;
        done_counter = N - F;
    }

    public void checkVal(PaxosValue val){
        if( val.view > viewVal.view){
            viewVal = val;
        }
    }

    public boolean changeLock(PaxosValue val){
        return val != viewVal;
    }

    public PaxosValue getViewVal(){
        return viewVal;
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
