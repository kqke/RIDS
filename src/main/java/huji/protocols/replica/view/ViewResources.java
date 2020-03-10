package huji.protocols.replica.view;

import java.util.HashMap;
import java.util.Map;

public class ViewResources {
    private Map<Integer,String> values;
    private Map<Integer,Integer> shared;

    private int ack_counter;
    private int elect_counter;

    public ViewResources() {
        values = new HashMap<>();
        shared = new HashMap<>();

        ack_counter = 0;
        elect_counter = 0;
    }

    public void add( int replica, String value ) {
        values.put(replica,value);
    }

    public void add( int replica, int value ) {
        shared.put(replica,value);
    }

    public boolean contains(int replica ) {
        return values.containsKey( replica );
    }

    public String get( int replica ) {
        return values.get(replica);
    }

    public boolean countdownACK() {
        return 0 == --ack_counter;
    }

    public boolean countdownELECT() {
        return 0 == --elect_counter;
    }
}
