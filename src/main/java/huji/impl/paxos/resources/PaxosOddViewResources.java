package huji.impl.paxos.resources;

import huji.impl.paxos.messages.PaxosValue;

import java.util.Hashtable;

public class PaxosOddViewResources {

    // Shares table
    private Hashtable<Integer, Integer> shares;

    // Odd view counters
    private int vote_counter;
    private int vc_state_counter;


    PaxosOddViewResources(int n, int f){
        vote_counter = n - f;
        vc_state_counter = n - f;
    }

    public void putShare(int from, int val){
        shares.put(from, val);
    }

    public Hashtable<Integer, Integer> getShares() {
        return shares;
    }

    public boolean countdownVote() {
        return 0 == --vote_counter;
    }

    public boolean countdownVCState() {
        return 0 == --vc_state_counter;
    }
}
