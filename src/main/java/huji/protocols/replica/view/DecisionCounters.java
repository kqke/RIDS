package huji.protocols.replica.view;

import java.util.HashMap;
import java.util.Map;

public class DecisionCounters {
    private Map<Integer, int[]> decision_counters;

    public DecisionCounters() {
        decision_counters = new HashMap<>();
    }

    public boolean voteCountdown( int view, int start_value ) {
        return countdown( view, 0, start_value );
    }

    public boolean vcCountdown( int view, int start_value ) {
        return countdown( view, 1, start_value );
    }

    private boolean countdown( int view, int idx, int start_value ) {
        int[] counters;
        if ( ( counters = get(view, start_value) ) == null )
            return false;

        if ( 0 != --counters[idx] )
            return false;

        decision_counters.put(view, null);
        return true;
    }

    private int[] get( int view, int start_value ) {
        decision_counters.putIfAbsent( view, new int[]{ start_value, start_value } );

        return decision_counters.get( view );
    }
}
