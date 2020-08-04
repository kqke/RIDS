package huji.impl.paxos.resources;

import huji.interfaces.Pair;

import java.util.HashMap;

import static huji.impl.paxos.resources.PaxosVCState.*;

public class PaxosViewResources<T extends Comparable<T>> {

    // Protocol parameters
    private final int N;
    private final int F;

    // Values table
    private final HashMap<Integer, Pair<T, PaxosVCState>> offer_values;

    // Shares table
    private final HashMap<Integer, Integer> shares;

    // Temp lock
    T temp_lock_val = null;
    int temp_lock_view = -1;

    // VC state
    private PaxosVCState vc_state;

    public PaxosViewResources(int n, int f){
        N = n;
        F = f;
        reset_counters();
        offer_values = new HashMap<>();
        shares = new HashMap<>();

        vc_state = NONE;
    }

    /* Ack offer */

    public void put_locked_ack(T value, int view) {
        if ( view > temp_lock_view ) {
            temp_lock_val = value;
            temp_lock_view = view;
        }
    }
    public boolean exists_locked_ack(){ return temp_lock_view != -1; }
    public T getLock(){ return temp_lock_val; }
    public int getLockView(){ return temp_lock_view; }

    /* Even */
    public void lock(int from, T val){
        offer_values.put(from, new Pair<>(val, LOCK));
    }

    public void done(int from, T val){
        offer_values.put(from, new Pair<>(val, DONE));
    }

    /* Secret share */
    public void put_secret(int from, int val){
        shares.put(from, val);
    }

    public HashMap<Integer, Integer> getShares() {
        return shares;
    }

    /* VC */

    public void putVCState(PaxosVCState type, T val, int leader){
        if ( val != null )
            temp_lock_val = val;

        if ( vc_state == DONE )
            return;

        switch(type) {
            case DONE:
                vc_state = DONE;
                break;
            case LOCK:
                if ( countdown_VCStateLock() )
                    vc_state = DONE;
                else
                    vc_state = LOCK;
                break;
        }
    }

    public PaxosVCState VCState(){
        return vc_state;
    }

    public T getPartyVal(int i){
        Pair<T, PaxosVCState> pair = offer_values.get(i);
        if ( pair != null )
            return pair.left;
        return null;
    }

    public PaxosVCState getPartyState(int i){
        Pair<T, PaxosVCState> pair = offer_values.get(i);
        if ( pair != null )
            return pair.right;
        return NONE;
    }

    /*
     * counters
     */

    private int ack_offer_counter;
    private int ack_lock_counter;
    private int done_counter;
    private int vote_counter;
    private int vc_state_counter;
    private int vc_state_lock_counter;

    private void reset_counters(){
        ack_offer_counter = N - F;
        ack_lock_counter = N - F;
        done_counter = N - F;
        vote_counter = N - F;
        vc_state_counter = N - F;
        vc_state_lock_counter = N - F;
    }

    public boolean countdown_AckOffer() { return 0 == --ack_offer_counter; }
    public boolean countdown_AckLock() { return 0 == --ack_lock_counter; }
    public boolean countdown_Done() { return 0 == --done_counter; }
    public boolean countdown_Vote() { return 0 == --vote_counter; }
    public boolean countdown_VCState() { return 0 == --vc_state_counter; }
    public boolean countdown_VCStateLock() { return 0 == --vc_state_lock_counter; }
}
