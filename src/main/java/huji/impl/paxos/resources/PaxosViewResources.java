package huji.impl.paxos.resources;

import huji.impl.paxos.messages.PaxosMessageType;
import huji.impl.paxos.messages.PaxosValue;
import javafx.util.Pair;

import java.util.HashMap;

import static huji.impl.paxos.resources.PaxosVCState.*;

public class PaxosViewResources {

    // Protocol parameters
    private final int N;
    private final int F;

    // Values table
    private HashMap<Integer, Pair<PaxosValue, PaxosVCState>> values;

    // Shares table
    private HashMap<Integer, Integer> shares;

    //Temp lock
    PaxosValue temp_lock_val;

    // Even view counters
    private int ack_offer_counter;
    private int ack_lock_counter;
    private int done_counter;

    // Odd view counters
    private int vote_counter;
    private int vc_state_counter;
    private int vc_state_lock_counter;

    // VC state
    private PaxosVCState vc_state;

    // View value
    private PaxosValue view_val;


    public PaxosViewResources(int n, int f){
        N = n;
        F = f;
        reset_counters();
        values = new HashMap<>();
        shares = new HashMap<>();

        vc_state = NONE;

        temp_lock_val = null;
    }

    private void reset_counters(){
        ack_offer_counter = N - F;
        ack_lock_counter = N - F;
        done_counter = N - F;
        vote_counter = N - F;
        vc_state_counter = N - F;
        vc_state_lock_counter = N - F;
    }


    public void putVal(int from, PaxosValue val){
        values.put(from, new Pair<>(val, NONE));
    }

    public void putLockedVal(PaxosValue val){
        if (temp_lock_val == null || val.view > temp_lock_val.view)
            temp_lock_val = val;
    }

    public boolean changeLock(){
        return temp_lock_val != null;
    }

    public PaxosValue getLock(){
        return temp_lock_val;
    }

    public void lock(int from, PaxosValue val){
        values.put(from, new Pair<>(val, LOCK));
    }

    public void done(int from, PaxosValue val){
        values.put(from, new Pair<>(val, DONE));
    }


    public void putShare(int from, int val){
        shares.put(from, val);
    }

    public HashMap<Integer, Integer> getShares() {
        return shares;
    }

    public void putVCState(PaxosMessageType type){
        if(vc_state == DONE)
            return;
        switch(type){
            case VC_STATE_DONE:
                vc_state = DONE;
                break;
            case VC_STATE_LOCK:
                if(countdownVCStateLock())
                    vc_state = DONE;
                else
                    if(vc_state != DONE)
                        vc_state = LOCK;
                break;
        }
    }

    public PaxosVCState VCState(){
        return vc_state;
    }

    public PaxosValue getPartyVal(int i){
        Pair<PaxosValue, PaxosVCState> pair = values.get(i);
        if(pair != null){
            return pair.getKey();
        }
        return null;
    }

    public PaxosVCState getPartyState(int i){
        Pair<PaxosValue, PaxosVCState> pair = values.get(i);
        if(pair != null){
            return pair.getValue();
        }
        return NONE;
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

    public boolean countdownVote() {
        return 0 == --vote_counter;
    }

    public boolean countdownVCState() {
        return 0 == --vc_state_counter;
    }

    public boolean countdownVCStateLock() {
        return 0 == --vc_state_lock_counter;
    }

}
