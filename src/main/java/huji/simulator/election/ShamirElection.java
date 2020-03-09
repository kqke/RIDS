package huji.simulator.election;

import huji.interfaces.Election;

import java.util.Map;

public class ShamirElection implements Election {
    final private int N;
    final private int F;

    private Map<Integer, int[]> election;

    public ShamirElection(int N, int F) {
        this.N = N;
        this.F = F;
    }

    @Override
    public int elect(int view) {
        return 0;
    }
}
