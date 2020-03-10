package huji.protocols.replica.view;

import java.util.HashMap;
import java.util.Map;

public class DecisionCounters {
    private Map<Integer, int[]> decision_counters;

    public DecisionCounters() {
        decision_counters = new HashMap<>();
    }

    public boolean voteCountdown( int view ) {
        return countdown( view, 0 );
    }

    public boolean vcCountdown( int view ) {
        return countdown( view, 1 );
    }

    private boolean countdown( int view, int idx ) {
        int[] counters;
        if ( ( counters = get(view) ) == null )
            return false;

        if ( 0 != --counters[idx] )
            return false;

        decision_counters.put(view, null);
        return true;
    }

    private int[] get( int view ) {
        decision_counters.putIfAbsent( view, new int[]{ 1, 1} );

        return decision_counters.get( view );
    }
}
