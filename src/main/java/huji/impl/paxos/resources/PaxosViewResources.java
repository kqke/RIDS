package huji.impl.paxos.resources;

import huji.impl.paxos.messages.PaxosValue;

import java.util.Hashtable;

public class PaxosViewResources {

    // Protocol parameters
    private final int N;
    private final int F;

    // Values table
    private Hashtable<Integer, PaxosValue> values;

    private PaxosEvenViewResources evenViewResources;
    private PaxosOddViewResources oddViewResources;

    private boolean isEvenView;

    private PaxosValue viewVal;

    public PaxosViewResources(int n, int f){
        N = n;
        F = f;
        values = new Hashtable<>();

        evenViewResources = new PaxosEvenViewResources(N, F);
        oddViewResources = new PaxosOddViewResources(N, F);
    }

    public void VC(){

    }

    public void putVal(int from, PaxosValue val){
        values.put(from, val);
    }

    public void putShare(int from, int val){
        oddViewResources.putShare(from, val);
    }

    public void putVCState(PaxosValue state){

    }

    public PaxosVCState VCState(){
        return null;
    }

    public Hashtable<Integer, Integer> getShares() {
        return oddViewResources.getShares();
    }

    public PaxosValue getViewVal(){
        return viewVal;
    }

    public PaxosValue getLeaderVal(){
        return null;
    }

    public boolean isLocked(int view){
        return viewVal.locked;
    }

    public boolean countdownAckOffer() {
        if(isEvenView)
            return evenViewResources.countdownAckOffer();
        return false;
    }

    public boolean countdownAckLock() {
        if(isEvenView)
            return evenViewResources.countdownAckLock();
        return false;
    }

    public boolean countdownDone() {
        if(isEvenView)
            return evenViewResources.countdownDone();
        return false;
    }

    public boolean countdownVote() {
        if (!isEvenView)
            return oddViewResources.countdownVote();
        return false;
    }

    public boolean countdownVCState() {
        if (!isEvenView)
            return oddViewResources.countdownVCState();
        return false;
    }

}
