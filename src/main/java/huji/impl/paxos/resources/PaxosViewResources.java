package huji.impl.paxos.resources;

import huji.interfaces.PairMap;

import java.util.HashMap;
import java.util.Map;

import static huji.impl.paxos.resources.PaxosVCState.*;

public class PaxosViewResources<T extends Comparable<T>> {
    private final int N;
    private final int F;

    public PaxosViewResources(int N, int F){
        this.N = N;
        this.F = F;
        resetCounters();
    }

    /* Ack offer */

    T temp_lock_val = null;
    int temp_lock_view = -1;

    public void putLockedAck(T value, int view) {
        if ( view > temp_lock_view ) {
            temp_lock_val = value;
            temp_lock_view = view;
        }
    }
    public boolean isExistsLockedAck(){ return temp_lock_view != -1; }
    public T getLock(){ return temp_lock_val; }
    public int getLockView(){ return temp_lock_view; }

    /* Even */

    private final PairMap<T, PaxosVCState> offer_values = new PairMap<>();

    public void lock(int from, T val) {
        offer_values.put(from, val, LOCK);
    }

    public void done(int from, T val) {
        offer_values.put(from, val, DONE);
    }

    public T getPartyValue(int i) {
        return offer_values.getLvalue(i);
    }

    public PaxosVCState getPartyState(int i) {
        return offer_values.getRvalueOrDefault(i, NONE);
    }

    /* Secret share */

    private final Map<Integer, Integer> shares = new HashMap<>();

    public void putSecret(int from, int val) {
        shares.put(from, val);
    }

    public Map<Integer, Integer> getShares() {
        return shares;
    }

    /* VC */

    private T leader_value = null;

    private boolean exists_done = false;
    private boolean exists_lock = false;
    private boolean all_lock = true;

    public void putVCState(PaxosVCState type, T val) {
        if ( val != null )
            leader_value = val;

        switch(type) {
            case DONE:
                exists_done = true;
                break;
            case LOCK:
                exists_lock = true;
                break;
            default:
                all_lock = false;
        }
    }

    public boolean isExistsDone() {
        return exists_done;
    }

    public boolean isExistsLock() {
        return exists_lock;
    }

    public boolean isAllLock() {
        return exists_lock && all_lock;
    }

    public T getVCValue(int leader) {
        if ( leader_value != null )
            return leader_value;
        return getPartyValue(leader);
    }

    /*
     * Counters
     */

    private int ack_offer_counter;
    private int ack_lock_counter;
    private int done_counter;
    private int vote_counter;
    private int vc_state_counter;

    private void resetCounters() {
        ack_offer_counter = N - F;
        ack_lock_counter = N - F;
        done_counter = N - F;
        vote_counter = N - F;
        vc_state_counter = N - F;
    }

    public boolean countdownAckOffer() { return 0 == --ack_offer_counter; }
    public boolean countdownAckLock() { return 0 == --ack_lock_counter; }
    public boolean countdownDone() { return 0 == --done_counter; }
    public boolean countdownVote() { return 0 == --vote_counter; }
    public boolean countdownVCState() { return 0 == --vc_state_counter; }
}
